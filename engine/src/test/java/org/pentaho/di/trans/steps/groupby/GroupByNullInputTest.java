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


package org.pentaho.di.trans.steps.groupby;

import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GroupByNullInputTest {

  public static final int NUMBER_OF_COLUMNS = 1;
  public static final String ANY_FIELD_NAME = "anyFieldName";
  static StepMockHelper<GroupByMeta, GroupByData> mockHelper;

  private GroupBy step;
  private GroupByData groupByStepData;

  private GroupByMeta groupByStepMeta;
  private RowMetaInterface rowMetaInterfaceMock;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    mockHelper = new StepMockHelper<>( "Group By", GroupByMeta.class, GroupByData.class );
    when( mockHelper.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) ).thenReturn(
      mockHelper.logChannelInterface );
    when( mockHelper.trans.isRunning() ).thenReturn( true );
  }

  @AfterClass
  public static void cleanUp() {
    mockHelper.cleanUp();
  }

  @Before
  public void setUp() throws Exception {
    groupByStepData = new GroupByData();
    groupByStepMeta = new GroupByMeta();

    when( mockHelper.stepMeta.getStepMetaInterface() ).thenReturn( groupByStepMeta );
    rowMetaInterfaceMock = Mockito.mock( RowMetaInterface.class );
    groupByStepData.inputRowMeta = rowMetaInterfaceMock;
    groupByStepData.aggMeta = rowMetaInterfaceMock;

    step = new GroupBy( mockHelper.stepMeta, groupByStepData, 0, mockHelper.transMeta, mockHelper.trans );
  }

  private void setAggregationTypesAndInitData( int[] aggregationTypes ) {
    //types of aggregation functions
    int countOfAggregations = aggregationTypes.length;
    groupByStepData.subjectnrs = new int[ countOfAggregations ];
    System.arraycopy( aggregationTypes, 0, groupByStepData.subjectnrs, 0, countOfAggregations );
    groupByStepMeta.setAggregateType( aggregationTypes );

    //init field names for aggregation columns
    String[] fieldNames = new String[ countOfAggregations ];
    Arrays.fill( fieldNames, ANY_FIELD_NAME );
    groupByStepMeta.setAggregateField( fieldNames );

    groupByStepData.subjectnrs = new int[ countOfAggregations ];
    //init sum arrays which are set on processRow which is not always called from tests
    groupByStepData.previousSums = new Object[ countOfAggregations ];
    groupByStepData.counts = new long[ countOfAggregations ];
    groupByStepData.previousAvgCount = new long[ countOfAggregations ];
    groupByStepData.previousAvgSum = new Object[ countOfAggregations ];
    Arrays.fill( groupByStepData.previousSums, 0 );
    Arrays.fill( groupByStepData.previousAvgCount, 0 );
    Arrays.fill( groupByStepData.previousAvgSum, 0 );
  }

  /**
   * PMD-1037 NPE error appears when user uses "Data Profile" feature to some tables in Hive 2.
   */
  @Test
  public void testNullInputDataForStandardDeviation() throws KettleValueException {
    setAggregationTypesAndInitData( new int[] { 15 } );
    ValueMetaInterface vmi = new ValueMetaInteger();
    when( rowMetaInterfaceMock.getValueMeta( Mockito.anyInt() ) ).thenReturn( vmi );
    Object[] row1 = new Object[ NUMBER_OF_COLUMNS ];
    Arrays.fill( row1, null );
    step.newAggregate( row1 );
    step.calcAggregate( row1 );
    Object[] aggregateResult = step.getAggregateResult();
    Assert.assertNull( "Returns null if aggregation is null", aggregateResult[0] );
  }

  @Test
  public void testNullInputDataForAggregationWithNumbers() throws KettleValueException {
    setAggregationTypesAndInitData( new int[] { 1, 2, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15 } );
    ValueMetaInterface vmi = new ValueMetaInteger();
    when( rowMetaInterfaceMock.getValueMeta( Mockito.anyInt() ) ).thenReturn( vmi );
    Object[] row1 = new Object[ NUMBER_OF_COLUMNS ];
    Arrays.fill( row1, null );
    step.newAggregate( row1 );
    step.calcAggregate( row1 );
    Object[] aggregateResult = step.getAggregateResult();
    Assert.assertNull( "Returns null if aggregation is null", aggregateResult[0] );
  }

  @Test
  public void testNullInputDataForAggregationWithNumbersMedianFunction() throws KettleValueException {
    setAggregationTypesAndInitData( new int[] { 3, 4 } );
    ValueMetaInterface vmi = new ValueMetaInteger();
    when( rowMetaInterfaceMock.getValueMeta( Mockito.anyInt() ) ).thenReturn( vmi );
    //PERCENTILE set
    groupByStepMeta.setValueField( new String[] { "3", "3" } );
    Object[] row1 = new Object[ NUMBER_OF_COLUMNS ];
    Arrays.fill( row1, null );
    step.newAggregate( row1 );
    step.calcAggregate( row1 );
    step.getAggregateResult();
  }

  @Test
  public void testNullInputDataForAggregationWithStrings() throws KettleValueException {
    setAggregationTypesAndInitData( new int[] { 8, 16, 17, 18 } );
    groupByStepMeta.setValueField( new String[] { "," } );
    groupByStepMeta.setSubjectField( new String[] { ANY_FIELD_NAME, ANY_FIELD_NAME } );
    ValueMetaInterface vmi = new ValueMetaString();
    when( rowMetaInterfaceMock.getValueMeta( Mockito.anyInt() ) ).thenReturn( vmi );
    Object[] row1 = new Object[ NUMBER_OF_COLUMNS ];
    Arrays.fill( row1, null );
    step.newAggregate( row1 );
    step.calcAggregate( row1 );
    Object[] row2 = new Object[ NUMBER_OF_COLUMNS ];
    Arrays.fill( row2, null );
    step.calcAggregate( row2 );
    Object[] row3 = new Object[ NUMBER_OF_COLUMNS ];
    Arrays.fill( row3, null );
    step.calcAggregate( row3 );
    step.getAggregateResult();
  }

}
