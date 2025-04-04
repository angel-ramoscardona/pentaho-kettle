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


package org.pentaho.di.www;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobConfiguration;
import org.pentaho.di.www.exception.DuplicateKeyException;

/**
 * This is a map between the job name and the (running/waiting/finished) job.
 *
 * @author Matt
 * @since 26-SEP-2007
 * @since 3.0.0
 *
 */
public class JobMap {
  private final Map<CarteObjectEntry, Job> jobMap;
  private final Map<CarteObjectEntry, JobConfiguration> configurationMap;

  private SlaveServerConfig slaveServerConfig;

  public JobMap() {
    jobMap = new HashMap<>();
    configurationMap = new HashMap<>();
  }

  public synchronized void addJob( String jobName, String carteObjectId, Job job, JobConfiguration jobConfiguration ) {
    CarteObjectEntry entry = new CarteObjectEntry( jobName, carteObjectId );
    jobMap.put( entry, job );
    configurationMap.put( entry, jobConfiguration );
  }

  public synchronized void registerJob( Job job, JobConfiguration jobConfiguration ) {
    job.setContainerObjectId( UUID.randomUUID().toString() );
    CarteObjectEntry entry = new CarteObjectEntry( job.getJobMeta().getName(), job.getContainerObjectId() );
    jobMap.put( entry, job );
    configurationMap.put( entry, jobConfiguration );
  }

  public synchronized void replaceJob( CarteObjectEntry entry, Job job, JobConfiguration jobConfiguration ) {
    jobMap.put( entry, job );
    configurationMap.put( entry, jobConfiguration );
  }

  /**
   * Find the first job in the list that comes to mind!
   *
   * @param jobName
   * @return the first transformation with the specified name
   */
  public synchronized Job getJob( String jobName ) {
    for ( CarteObjectEntry entry : jobMap.keySet() ) {
      if ( entry.getName().equals( jobName ) ) {
        return getJob( entry );
      }
    }
    return null;
  }

  /**
   * @param entry
   *          The Carte job object
   * @return the job with the specified entry
   */
  public synchronized Job getJob( CarteObjectEntry entry ) {
    return jobMap.get( entry );
  }

  public synchronized JobConfiguration getConfiguration( String jobName ) {
    for ( CarteObjectEntry entry : configurationMap.keySet() ) {
      if ( entry.getName().equals( jobName ) ) {
        return getConfiguration( entry );
      }
    }
    return null;
  }

  /**
   * @param entry
   *          The Carte job object
   * @return the job configuration with the specified entry
   */
  public synchronized JobConfiguration getConfiguration( CarteObjectEntry entry ) {
    return configurationMap.get( entry );
  }

  public synchronized void removeJob( CarteObjectEntry entry ) {
    jobMap.remove( entry );
    configurationMap.remove( entry );
  }

  public synchronized List<CarteObjectEntry> getJobObjects() {
    return new ArrayList<>( jobMap.keySet() );
  }

  public synchronized CarteObjectEntry getFirstCarteObjectEntry( String jobName ) {
    for ( CarteObjectEntry key : jobMap.keySet() ) {
      if ( key.getName().equals( jobName ) ) {
        return key;
      }
    }
    return null;
  }

  public synchronized CarteObjectEntry getUniqueCarteObjectEntry( String jobName ) throws DuplicateKeyException {
    CarteObjectEntry entry = null;
    boolean unique = true;

    for ( CarteObjectEntry key : jobMap.keySet() ) {
      if ( unique && key.getName().equals( jobName ) ) {
        unique = false;
        entry = key;
      } else if ( !unique && key.getName().equals( jobName ) ) {
        throw new DuplicateKeyException();
      }
    }
    return entry;
  }

  /**
   * @return the slaveServerConfig
   */
  public SlaveServerConfig getSlaveServerConfig() {
    return slaveServerConfig;
  }

  /**
   * @param slaveServerConfig
   *          the slaveServerConfig to set
   */
  public void setSlaveServerConfig( SlaveServerConfig slaveServerConfig ) {
    this.slaveServerConfig = slaveServerConfig;
  }

  /**
   * Find a job using the container/carte object ID.
   *
   * @param id
   *          the container/carte object ID
   * @return The job if it's found, null if the ID couldn't be found in the job map.
   */
  public synchronized Job findJob( String id ) {
    for ( Job job : jobMap.values() ) {
      if ( job.getContainerObjectId().equals( id ) ) {
        return job;
      }
    }
    return null;
  }

}
