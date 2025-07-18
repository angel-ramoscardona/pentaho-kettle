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


package org.pentaho.di.trans.steps.gettablenames;

import com.google.common.annotations.VisibleForTesting;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

/**
 * Return tables name list from Database connection *
 *
 * @author Samatar
 * @since 03-Juin-2008
 *
 */

public class GetTableNames extends BaseDatabaseStep implements StepInterface {
  private static Class<?> PKG = GetTableNamesMeta.class; // for i18n purposes, needed by Translator2!!

  private GetTableNamesMeta meta;
  private GetTableNamesData data;

  public GetTableNames( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
    Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  /**
   * Build an empty row based on the meta-data...
   *
   * @return
   */

  private Object[] buildEmptyRow() {
    Object[] rowData = RowDataUtil.allocateRowData( data.outputRowMeta.size() );

    return rowData;
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (GetTableNamesMeta) smi;
    data = (GetTableNamesData) sdi;

    if ( meta.isDynamicSchema() ) {
      // Grab one row from previous step ...
      data.readrow = getRow();

      if ( data.readrow == null ) {
        setOutputDone();
        return false;
      }
    }

    if ( first ) {
      first = false;

      if ( meta.isDynamicSchema() ) {
        data.inputRowMeta = getInputRowMeta();
        data.outputRowMeta = data.inputRowMeta.clone();
        // Get total previous fields
        data.totalpreviousfields = data.inputRowMeta.size();

        // Check is filename field is provided
        if ( Utils.isEmpty( meta.getSchemaFieldName() ) ) {
          logError( BaseMessages.getString( PKG, "GetTableNames.Log.NoSchemaField" ) );
          throw new KettleException( BaseMessages.getString( PKG, "GetTableNames.Log.NoSchemaField" ) );
        }

        // cache the position of the field
        if ( data.indexOfSchemaField < 0 ) {
          data.indexOfSchemaField = data.inputRowMeta.indexOfValue( meta.getSchemaFieldName() );
          if ( data.indexOfSchemaField < 0 ) {
            // The field is unreachable !
            logError( BaseMessages.getString( PKG, "GetTableNames.Log.ErrorFindingField" )
              + "[" + meta.getSchemaFieldName() + "]" );
            throw new KettleException( BaseMessages.getString(
              PKG, "GetTableNames.Exception.CouldnotFindField", meta.getSchemaFieldName() ) );
          }
        }

      } else {
        data.outputRowMeta = new RowMeta();
      }

      meta.getFields( getTransMeta().getBowl(), data.outputRowMeta, getStepname(), null, null, this, repository,
        metaStore );

    }

    if ( meta.isDynamicSchema() ) {
      // Get value of dynamic schema ...
      data.realSchemaName = data.inputRowMeta.getString( data.readrow, data.indexOfSchemaField );
    }

    Object[] outputRow = buildEmptyRow();
    if ( meta.isDynamicSchema() ) {
      System.arraycopy( data.readrow, 0, outputRow, 0, data.readrow.length );
    }
    processIncludeCatalog( outputRow );
    processIncludeSchema( outputRow );
    processIncludeTable( outputRow );
    processIncludeView( outputRow );
    processIncludeProcedure( outputRow );
    processIncludeSynonym( outputRow );

    if ( !meta.isDynamicSchema() ) {
      setOutputDone();
      return false;
    } else {
      return true;
    }
  }

  private void processIncludeSynonym( Object[] outputRow )
    throws KettleDatabaseException, KettleStepException, KettleValueException {
    if ( meta.isIncludeSynonym() ) {
      String[] synonyms = data.db.getSynonyms( data.realSchemaName, meta.isAddSchemaInOut() );
      String ObjectType = BaseMessages.getString( PKG, "GetTableNamesDialog.ObjectType.Synonym" );

      for ( int i = 0; i < synonyms.length && !isStopped(); i++ ) {
        Object[] outputRowSyn = outputRow.clone();
        int outputIndex = data.totalpreviousfields;

        String synonym = synonyms[i];

        outputRowSyn[outputIndex++] = synonym;

        if ( !Utils.isEmpty( data.realObjectTypeFieldName ) ) {
          outputRowSyn[outputIndex++] = ObjectType;
        }
        if ( !Utils.isEmpty( data.realIsSystemObjectFieldName ) ) {
          outputRowSyn[outputIndex++] = Boolean.valueOf( data.db.isSystemTable( synonym ) );
        }
        if ( !Utils.isEmpty( data.realSQLCreationFieldName ) ) {
          outputRowSyn[outputIndex++] = null;
        }
        data.rownr++;
        putRow( data.outputRowMeta, outputRowSyn ); // copy row to output rowset(s);

        logInfo( outputRowSyn );
      }
    }
  }

  private void processIncludeProcedure( Object[] outputRow )
    throws KettleDatabaseException, KettleStepException, KettleValueException {
    if ( meta.isIncludeProcedure() ) {
      String[] procNames = data.db.getProcedures();
      String ObjectType = BaseMessages.getString( PKG, "GetTableNamesDialog.ObjectType.Procedure" );
      for ( int i = 0; i < procNames.length && !isStopped(); i++ ) {
        Object[] outputRowProc = outputRow.clone();
        int outputIndex = data.totalpreviousfields;

        String procName = procNames[i];
        outputRowProc[outputIndex++] = procName;

        if ( !Utils.isEmpty( data.realObjectTypeFieldName ) ) {
          outputRowProc[outputIndex++] = ObjectType;
        }
        if ( !Utils.isEmpty( data.realIsSystemObjectFieldName ) ) {
          outputRowProc[outputIndex++] = Boolean.valueOf( data.db.isSystemTable( procName ) );
        }
        if ( !Utils.isEmpty( data.realSQLCreationFieldName ) ) {
          outputRowProc[outputIndex++] = null;
        }
        data.rownr++;
        putRow( data.outputRowMeta, outputRowProc ); // copy row to output rowset(s);

        logInfo( outputRowProc );
      }
    }
  }

  @VisibleForTesting
  void processIncludeView( Object[] outputRow ) {
    // Views
    if ( meta.isIncludeView() ) {
      try {
        String[] viewNames = data.db.getViews( data.realSchemaName, meta.isAddSchemaInOut() );
        String[] viewNamesWithoutSchema = data.db.getViews( data.realSchemaName, false );
        String ObjectType = BaseMessages.getString( PKG, "GetTableNamesDialog.ObjectType.View" );
        for ( int i = 0; i < viewNames.length && !isStopped(); i++ ) {
          Object[] outputRowView = outputRow.clone();
          int outputIndex = data.totalpreviousfields;

          String viewName = viewNames[i];
          String viewNameWithoutSchema = viewNamesWithoutSchema[i];
          outputRowView[outputIndex++] = viewName;

          if ( !Utils.isEmpty( data.realObjectTypeFieldName ) ) {
            outputRowView[outputIndex++] = ObjectType;
          }
          if ( !Utils.isEmpty( data.realIsSystemObjectFieldName ) ) {
            outputRowView[outputIndex++] = Boolean.valueOf( data.db.isSystemTable( viewNameWithoutSchema ) );
          }

          if ( !Utils.isEmpty( data.realSQLCreationFieldName ) ) {
            outputRowView[outputIndex++] = null;
          }
          data.rownr++;
          putRow( data.outputRowMeta, outputRowView ); // copy row to output rowset(s);

          logInfo( outputRowView );
        }
      } catch ( Exception e ) {
        // Ignore
      }
    }
  }

  @VisibleForTesting
  void processIncludeTable( Object[] outputRow )
    throws KettleDatabaseException, KettleStepException, KettleValueException {
    if ( meta.isIncludeTable() ) {
      // Tables

      String[] tableNames = data.db.getTablenames( data.realSchemaName, meta.isAddSchemaInOut() );
      String[] tableNamesWithoutSchema = data.db.getTablenames( data.realSchemaName, false );

      String ObjectType = BaseMessages.getString( PKG, "GetTableNamesDialog.ObjectType.Table" );

      for ( int i = 0; i < tableNames.length && !isStopped(); i++ ) {
        Object[] outputRowTable = outputRow.clone();

        int outputIndex = data.totalpreviousfields;

        String tableName = tableNames[i];
        String tableNameWithoutSchema = tableNamesWithoutSchema[i];
        outputRowTable[outputIndex++] = tableName;

        if ( !Utils.isEmpty( data.realObjectTypeFieldName ) ) {
          outputRowTable[outputIndex++] = ObjectType;
        }
        if ( !Utils.isEmpty( data.realIsSystemObjectFieldName ) ) {
          outputRowTable[outputIndex++] = Boolean.valueOf( data.db.isSystemTable( tableNameWithoutSchema ) );
        }
        // Get primary key
        String pk = null;
        String[] pkc = data.db.getPrimaryKeyColumnNames( tableNameWithoutSchema );
        if ( pkc != null && pkc.length == 1 ) {
          pk = pkc[0];
        }
        // return sql creation
        // handle simple primary key (one field)
        String sql =
          data.db
            .getCreateTableStatement(
              tableName,
              data.db.getTableFieldsMeta( data.realSchemaName, tableNameWithoutSchema ),
              null, false, pk, true );

        if ( pkc != null && pkc.length > 0 ) {
          // add composite primary key (several fields in primary key)
          int IndexOfLastClosedBracket = sql.lastIndexOf( ")" );
          if ( IndexOfLastClosedBracket > -1 ) {
            sql = sql.substring( 0, IndexOfLastClosedBracket );
            sql += ", PRIMARY KEY (";
            for ( int k = 0; k < pkc.length; k++ ) {
              if ( k > 0 ) {
                sql += ", ";
              }
              sql += pkc[k];
            }
            sql += ")" + Const.CR + ")" + Const.CR + ";";
          }
        }
        if ( !Utils.isEmpty( data.realSQLCreationFieldName ) ) {
          outputRowTable[outputIndex++] = sql;
        }

        data.rownr++;
        putRow( data.outputRowMeta, outputRowTable ); // copy row to output rowset(s);

        logInfo( outputRowTable );
      }
    }
  }

  private void processIncludeSchema( Object[] outputRow )
    throws KettleDatabaseException, KettleStepException, KettleValueException {
    // Schemas
    if ( meta.isIncludeSchema() ) {
      String ObjectType = BaseMessages.getString( PKG, "GetTableNamesDialog.ObjectType.Schema" );
      // Views
      String[] schemaNames = new String[] {};
      if ( !Utils.isEmpty( data.realSchemaName ) ) {
        schemaNames = new String[] { data.realSchemaName };
      } else {
        schemaNames = data.db.getSchemas();
      }
      for ( int i = 0; i < schemaNames.length && !isStopped(); i++ ) {

        // Clone current input row
        Object[] outputRowSchema = outputRow.clone();

        int outputIndex = data.totalpreviousfields;

        String schemaName = schemaNames[i];
        outputRowSchema[outputIndex++] = schemaName;

        if ( !Utils.isEmpty( data.realObjectTypeFieldName ) ) {
          outputRowSchema[outputIndex++] = ObjectType;
        }
        if ( !Utils.isEmpty( data.realIsSystemObjectFieldName ) ) {
          outputRowSchema[outputIndex++] = Boolean.valueOf( data.db.isSystemTable( schemaName ) );
        }
        if ( !Utils.isEmpty( data.realSQLCreationFieldName ) ) {
          outputRowSchema[outputIndex++] = null;
        }
        data.rownr++;
        putRow( data.outputRowMeta, outputRowSchema ); // copy row to output rowset(s);

        logInfo( outputRowSchema );
      }
    }
  }

  private void processIncludeCatalog( Object[] outputRow )
    throws KettleDatabaseException, KettleStepException, KettleValueException {
    // Catalogs
    if ( meta.isIncludeCatalog() ) {
      String ObjectType = BaseMessages.getString( PKG, "GetTableNames.ObjectType.Catalog" );
      // Views
      String[] catalogsNames = data.db.getCatalogs();

      for ( int i = 0; i < catalogsNames.length && !isStopped(); i++ ) {

        // Clone current input row
        Object[] outputRowCatalog = outputRow.clone();

        int outputIndex = data.totalpreviousfields;

        String catalogName = catalogsNames[i];
        outputRowCatalog[outputIndex++] = catalogName;

        if ( !Utils.isEmpty( data.realObjectTypeFieldName ) ) {
          outputRowCatalog[outputIndex++] = ObjectType;
        }
        if ( !Utils.isEmpty( data.realIsSystemObjectFieldName ) ) {
          outputRowCatalog[outputIndex++] = Boolean.valueOf( data.db.isSystemTable( catalogName ) );
        }
        if ( !Utils.isEmpty( data.realSQLCreationFieldName ) ) {
          outputRowCatalog[outputIndex++] = null;
        }
        data.rownr++;
        putRow( data.outputRowMeta, outputRowCatalog ); // copy row to output rowset(s);

        logInfo( outputRowCatalog );
      }
    }
  }

  private void logInfo( Object[] outputRow ) throws KettleValueException {
    if ( checkFeedback( getLinesRead() ) ) {
      if ( log.isDetailed() ) {
        logDetailed( BaseMessages.getString( PKG, "GetTableNames.LineNumber", "" + getLinesRead() ) );
      }
    }
    if ( log.isRowLevel() ) {
      logRowlevel( BaseMessages.getString( PKG, "GetTableNames.Log.PutoutRow", data.outputRowMeta
        .getString( outputRow ) ) );
    }
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (GetTableNamesMeta) smi;
    data = (GetTableNamesData) sdi;

    if ( super.init( smi, sdi ) ) {
      if ( Utils.isEmpty( meta.getTablenameFieldName() ) ) {
        logError( BaseMessages.getString( PKG, "GetTableNames.Error.TablenameFieldNameMissing" ) );
        return false;
      }
      String realSchemaName = environmentSubstitute( meta.getSchemaName() );
      if ( !Utils.isEmpty( realSchemaName ) ) {
        data.realSchemaName = realSchemaName;
      }
      data.realTableNameFieldName = environmentSubstitute( meta.getTablenameFieldName() );
      data.realObjectTypeFieldName = environmentSubstitute( meta.getObjectTypeFieldName() );
      data.realIsSystemObjectFieldName = environmentSubstitute( meta.isSystemObjectFieldName() );
      data.realSQLCreationFieldName = environmentSubstitute( meta.getSQLCreationFieldName() );
      if ( !meta.isIncludeCatalog()
        && !meta.isIncludeSchema() && !meta.isIncludeTable() && !meta.isIncludeView()
        && !meta.isIncludeProcedure() && !meta.isIncludeSynonym() ) {
        logError( BaseMessages.getString( PKG, "GetTableNames.Error.includeAtLeastOneType" ) );
        return false;
      }

      try {
        // Create the output row meta-data
        data.outputRowMeta = new RowMeta();
        // get the metadata populated
        meta.getFields( getTransMeta().getBowl(), data.outputRowMeta, getStepname(), null, null, this, repository,
          metaStore );
      } catch ( KettleException e ) {
        logError( "Error initializing step: " + e.toString() );
        logError( Const.getStackTracker( e ) );
        return false;
      }
      return true;
    }
    return false;
  }

  @Override
  protected Class<?> getPKG() {
    return PKG;
  }

}
