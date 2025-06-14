/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.job.entries.delay;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.job.entry.validator.AndValidator;
import org.pentaho.di.job.entry.validator.JobEntryValidatorUtils;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * Job entry type to sleep for a time. It uses a piece of javascript to do this.
 *
 * @author Samatar
 * @since 21-02-2007
 */
public class JobEntryDelay extends JobEntryBase implements Cloneable, JobEntryInterface {
  private static Class<?> PKG = JobEntryDelay.class; // for i18n purposes, needed by Translator2!!

  private static String DEFAULT_MAXIMUM_TIMEOUT = "0";

  private String maximumTimeout; // maximum timeout in seconds

  public int scaleTime;

  public JobEntryDelay( String n ) {
    super( n, "" );
  }

  public JobEntryDelay() {
    this( "" );
  }

  @Override
  public Object clone() {
    JobEntryDelay je = (JobEntryDelay) super.clone();
    return je;
  }

  @Override
  public String getXML() {
    StringBuilder retval = new StringBuilder( 200 );

    retval.append( super.getXML() );
    retval.append( "      " ).append( XMLHandler.addTagValue( "maximumTimeout", maximumTimeout ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "scaletime", scaleTime ) );

    return retval.toString();
  }

  @Override
  public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers,
    Repository rep, IMetaStore metaStore ) throws KettleXMLException {
    try {
      super.loadXML( entrynode, databases, slaveServers );
      maximumTimeout = XMLHandler.getTagValue( entrynode, "maximumTimeout" );
      scaleTime = Integer.parseInt( XMLHandler.getTagValue( entrynode, "scaletime" ) );
    } catch ( Exception e ) {
      throw new KettleXMLException( BaseMessages.getString( PKG, "JobEntryDelay.UnableToLoadFromXml.Label" ), e );
    }
  }

  @Override
  public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases,
    List<SlaveServer> slaveServers ) throws KettleException {
    try {
      maximumTimeout = rep.getJobEntryAttributeString( id_jobentry, "maximumTimeout" );
      scaleTime = (int) rep.getJobEntryAttributeInteger( id_jobentry, "scaletime" );
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( BaseMessages.getString( PKG, "JobEntryDelay.UnableToLoadFromRepo.Label" )
        + id_jobentry, dbe );
    }
  }

  //
  // Save the attributes of this job entry
  //
  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
    try {
      rep.saveJobEntryAttribute( id_job, getObjectId(), "maximumTimeout", maximumTimeout );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "scaletime", scaleTime );
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException(
        BaseMessages.getString( PKG, "JobEntryDelay.UnableToSaveToRepo.Label" ) + id_job, dbe );
    }
  }

  /**
   * Execute this job entry and return the result. In this case it means, just set the result boolean in the Result
   * class.
   *
   * @param previousResult
   *          The result of the previous execution
   * @return The Result of the execution.
   */
  @Override
  public Result execute( Result previousResult, int nr ) {
    Result result = previousResult;
    result.setResult( false );
    int multiple;
    String waitScale;

    // Validate if Real Maximum Timeout is only digits.
    if ( !NumberUtils.isDigits( getRealMaximumTimeout() ) ) {
      result.setResult( false );
      result.setNrErrors( 1 );
      logError( "Invalid value for Maximum Timeout." );
      return result;
    }

    // Scale time
    switch ( scaleTime ) {
      case 0:
        // Second
        multiple = 1000;
        waitScale = BaseMessages.getString( PKG, "JobEntryDelay.SScaleTime.Label" );
        break;
      case 1:
        // Minute
        multiple = 60000;
        waitScale = BaseMessages.getString( PKG, "JobEntryDelay.MnScaleTime.Label" );
        break;
      default:
        // Hour
        multiple = 3600000;
        waitScale = BaseMessages.getString( PKG, "JobEntryDelay.HrScaleTime.Label" );
        break;
    }

    try {
      // start time (in seconds, Minutes or Hours)
      double timeStart = (double) System.currentTimeMillis() / (double) multiple;

      double iMaximumTimeout = Const.toInt( getRealMaximumTimeout(), Const.toInt( DEFAULT_MAXIMUM_TIMEOUT, 0 ) );

      if ( isDetailed() ) {
        logDetailed( BaseMessages.getString( PKG, "JobEntryDelay.LetsWaitFor.Label", iMaximumTimeout, waitScale ) );
      }

      boolean continueLoop = true;
      //
      // Sanity check on some values, and complain on insanity
      //
      if ( iMaximumTimeout < 0 ) {
        iMaximumTimeout = Const.toInt( DEFAULT_MAXIMUM_TIMEOUT, 0 );
        logBasic( BaseMessages.getString( PKG, "JobEntryDelay.MaximumTimeReset.Label", String
          .valueOf( iMaximumTimeout ), String.valueOf( waitScale ) ) );
      }

      // Loop until the delay time has expired.
      //
      while ( continueLoop && !parentJob.isStopped() ) {
        // Update Time value
        double now = (double) System.currentTimeMillis() / (double) multiple;

        // Let's check the limit time
        if ( ( iMaximumTimeout >= 0 ) && ( now >= ( timeStart + iMaximumTimeout ) ) ) {
          // We have reached the time limit
          if ( log.isDetailed() ) {
            logDetailed( BaseMessages.getString( PKG, "JobEntryDelay.WaitTimeIsElapsed.Label" ) );
          }
          continueLoop = false;
          result.setResult( true );
        } else {
          Thread.sleep( 100 );
        }
      }
    } catch ( Exception e ) {
      // We get an exception
      result.setResult( false );
      logError( "Error  : " + e.getMessage() );

      if ( Thread.currentThread().isInterrupted() ) {
        Thread.currentThread().interrupt();
      }
    }

    return result;
  }

  @Override
  public boolean resetErrorsBeforeExecution() {
    // we should be able to evaluate the errors in
    // the previous jobentry.
    return false;
  }

  @Override
  public boolean evaluates() {
    return true;
  }

  @Override
  public boolean isUnconditional() {
    return false;
  }

  public String getMaximumTimeout() {
    return maximumTimeout;
  }

  public String getRealMaximumTimeout() {
    return Const.trim( environmentSubstitute( getMaximumTimeout() ) );
  }

  @Deprecated
  public String getrealMaximumTimeout() {
    return getRealMaximumTimeout();
  }

  public void setMaximumTimeout( String s ) {
    maximumTimeout = s;
  }

  @Override
  public void check( List<CheckResultInterface> remarks, JobMeta jobMeta, VariableSpace space,
    Repository repository, IMetaStore metaStore ) {
    JobEntryValidatorUtils.andValidator().validate( jobMeta.getBowl(), this, "maximumTimeout", remarks,
        AndValidator.putValidators( JobEntryValidatorUtils.longValidator() ) );
    JobEntryValidatorUtils.andValidator().validate( jobMeta.getBowl(), this, "scaleTime", remarks,
        AndValidator.putValidators( JobEntryValidatorUtils.integerValidator() ) );
  }

  public int getScaleTime() {
    return scaleTime;
  }

  public void setScaleTime( int scaleTime ) {
    this.scaleTime = scaleTime;
  }
}
