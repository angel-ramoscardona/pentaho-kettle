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


package org.pentaho.di.trans.steps.nullif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.RowProducer;
import org.pentaho.di.trans.RowStepCollector;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.injector.InjectorMeta;
import org.pentaho.di.trans.steps.nullif.NullIfMeta.Field;

import junit.framework.TestCase;

/**
 * Test class for the NullIf step.
 *
 * @author Sven Boden
 */
public class NullIfIT extends TestCase {
  public RowMetaInterface createRowMetaInterface() {
    RowMetaInterface rm = new RowMeta();

    ValueMetaInterface[] valuesMeta =
    { new ValueMetaString( "field1" ), new ValueMetaInteger( "field2" ), };

    for ( int i = 0; i < valuesMeta.length; i++ ) {
      rm.addValueMeta( valuesMeta[i] );
    }

    return rm;
  }

  public List<RowMetaAndData> createData() {
    List<RowMetaAndData> list = new ArrayList<RowMetaAndData>();

    RowMetaInterface rm = createRowMetaInterface();

    Object[] r1 = new Object[] { "abc", new Long( 1L ) };
    Object[] r2 = new Object[] { "ABC", new Long( 1L ) };
    Object[] r3 = new Object[] { "tst", new Long( 2L ) };
    Object[] r4 = new Object[] { "tst", new Long( 3L ) };
    Object[] r5 = new Object[] { "TST", new Long( 2L ) };
    Object[] r6 = new Object[] { "tst", new Long( 1L ) };
    Object[] r7 = new Object[] { "", null };
    Object[] r8 = new Object[] { null, new Long( 8L ) };
    Object[] r9 = new Object[] { "abc", new Long( 2L ) };

    list.add( new RowMetaAndData( rm, r1 ) );
    list.add( new RowMetaAndData( rm, r2 ) );
    list.add( new RowMetaAndData( rm, r3 ) );
    list.add( new RowMetaAndData( rm, r4 ) );
    list.add( new RowMetaAndData( rm, r5 ) );
    list.add( new RowMetaAndData( rm, r6 ) );
    list.add( new RowMetaAndData( rm, r7 ) );
    list.add( new RowMetaAndData( rm, r8 ) );
    list.add( new RowMetaAndData( rm, r9 ) );

    return list;
  }

  /**
   * Create result data for test case 1.
   */
  public List<RowMetaAndData> createResultData1() {
    List<RowMetaAndData> list = new ArrayList<RowMetaAndData>();

    RowMetaInterface rm = createRowMetaInterface();

    Object[] r1 = new Object[] { "abc", null };
    Object[] r2 = new Object[] { "ABC", null };
    Object[] r3 = new Object[] { null, new Long( 2L ) };
    Object[] r4 = new Object[] { null, new Long( 3L ) };
    Object[] r5 = new Object[] { "TST", new Long( 2L ) };
    Object[] r6 = new Object[] { null, null };
    Object[] r7 = new Object[] { "", null };
    Object[] r8 = new Object[] { null, new Long( 8L ) };
    Object[] r9 = new Object[] { "abc", new Long( 2L ) };

    list.add( new RowMetaAndData( rm, r1 ) );
    list.add( new RowMetaAndData( rm, r2 ) );
    list.add( new RowMetaAndData( rm, r3 ) );
    list.add( new RowMetaAndData( rm, r4 ) );
    list.add( new RowMetaAndData( rm, r5 ) );
    list.add( new RowMetaAndData( rm, r6 ) );
    list.add( new RowMetaAndData( rm, r7 ) );
    list.add( new RowMetaAndData( rm, r8 ) );
    list.add( new RowMetaAndData( rm, r9 ) );

    return list;
  }

  /**
   * Check the 2 lists comparing the rows in order. If they are not the same fail the test.
   */
  public void checkRows( List<RowMetaAndData> rows1, List<RowMetaAndData> rows2 ) {
    int idx = 1;
    if ( rows1.size() != rows2.size() ) {
      fail( "Number of rows is not the same: " + rows1.size() + " and " + rows2.size() );
    }
    Iterator<RowMetaAndData> it1 = rows1.iterator();
    Iterator<RowMetaAndData> it2 = rows2.iterator();

    while ( it1.hasNext() && it2.hasNext() ) {
      RowMetaAndData rm1 = it1.next();
      RowMetaAndData rm2 = it2.next();

      Object[] r1 = rm1.getData();
      Object[] r2 = rm2.getData();

      if ( rm1.size() != rm2.size() ) {
        fail( "row nr " + idx + " is not equal" );
      }
      int[] fields = new int[r1.length];
      for ( int ydx = 0; ydx < r1.length; ydx++ ) {
        fields[ydx] = ydx;
      }
      try {
        if ( rm1.getRowMeta().compare( r1, r2, fields ) != 0 ) {
          fail( "row nr " + idx + " is not equal" );
        }
      } catch ( KettleValueException e ) {
        fail( "row nr " + idx + " is not equal" );
      }

      idx++;
    }
  }

  /**
   * Test case for nullif step. Injector step to a nullif step to a dummy step.
   */
  public void testNullIf1() throws Exception {
    KettleEnvironment.init();

    //
    // Create a new transformation...
    //
    TransMeta transMeta = new TransMeta();
    transMeta.setName( "nulliftest1" );

    PluginRegistry registry = PluginRegistry.getInstance();

    //
    // create an injector step...
    //
    String injectorStepname = "injector step";
    InjectorMeta im = new InjectorMeta();

    // Set the information of the injector.
    String injectorPid = registry.getPluginId( StepPluginType.class, im );
    StepMeta injectorStep = new StepMeta( injectorPid, injectorStepname, im );
    transMeta.addStep( injectorStep );

    //
    // Create a dummy step 1
    //
    String dummyStepname1 = "dummy step 1";
    DummyTransMeta dm1 = new DummyTransMeta();

    String dummyPid1 = registry.getPluginId( StepPluginType.class, dm1 );
    StepMeta dummyStep1 = new StepMeta( dummyPid1, dummyStepname1, dm1 );
    transMeta.addStep( dummyStep1 );

    TransHopMeta hi = new TransHopMeta( injectorStep, dummyStep1 );
    transMeta.addTransHop( hi );

    //
    // Create a NullIf step
    //
    String nullIfName = "nullif step";
    NullIfMeta ni = new NullIfMeta();

    Field[] fields = new Field[2];
    fields[0] = new Field();
    fields[0].setFieldName( "field2" );
    fields[0].setFieldValue( "1" );
    fields[1] = new Field();
    fields[1].setFieldName( "field1" );
    fields[1].setFieldValue( "tst" );
    ni.setFields( fields );

    String nullIfPid = registry.getPluginId( StepPluginType.class, ni );
    StepMeta nullIfStep = new StepMeta( nullIfPid, nullIfName, ni );
    transMeta.addStep( nullIfStep );

    TransHopMeta hi2 = new TransHopMeta( dummyStep1, nullIfStep );
    transMeta.addTransHop( hi2 );

    //
    // Create a dummy step 2
    //
    String dummyStepname2 = "dummy step 2";
    DummyTransMeta dm2 = new DummyTransMeta();

    String dummyPid2 = registry.getPluginId( StepPluginType.class, dm2 );
    StepMeta dummyStep2 = new StepMeta( dummyPid2, dummyStepname2, dm2 );
    transMeta.addStep( dummyStep2 );

    TransHopMeta hi3 = new TransHopMeta( nullIfStep, dummyStep2 );
    transMeta.addTransHop( hi3 );

    // Now execute the transformation...
    Trans trans = new Trans( transMeta );

    trans.prepareExecution( null );

    StepInterface si = trans.getStepInterface( dummyStepname1, 0 );
    RowStepCollector dummyRc1 = new RowStepCollector();
    si.addRowListener( dummyRc1 );

    si = trans.getStepInterface( nullIfName, 0 );
    RowStepCollector nullIfRc = new RowStepCollector();
    si.addRowListener( nullIfRc );

    si = trans.getStepInterface( dummyStepname2, 0 );
    RowStepCollector dummyRc = new RowStepCollector();
    si.addRowListener( dummyRc );

    RowProducer rp = trans.addRowProducer( injectorStepname, 0 );
    trans.startThreads();

    // add rows
    List<RowMetaAndData> inputList = createData();
    Iterator<RowMetaAndData> it = inputList.iterator();
    while ( it.hasNext() ) {
      RowMetaAndData rm = it.next();
      rp.putRow( rm.getRowMeta(), rm.getData() );
    }
    rp.finished();

    trans.waitUntilFinished();

    // Compare the results
    List<RowMetaAndData> resultRows = dummyRc.getRowsWritten();
    List<RowMetaAndData> goldenImageRows = createResultData1();

    checkRows( goldenImageRows, resultRows );
  }
}
