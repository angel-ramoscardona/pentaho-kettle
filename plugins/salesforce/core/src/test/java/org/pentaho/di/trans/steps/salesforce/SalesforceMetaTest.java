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


package org.pentaho.di.trans.steps.salesforce;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.value.ValueMetaPluginType;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;

public class SalesforceMetaTest {
  @ClassRule public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    PluginRegistry.addPluginType( ValueMetaPluginType.getInstance() );
    PluginRegistry.addPluginType( TwoWayPasswordEncoderPluginType.getInstance() );
    PluginRegistry.init( true );
    String passwordEncoderPluginID =
      Const.NVL( EnvUtil.getSystemProperty( Const.KETTLE_PASSWORD_ENCODER_PLUGIN ), "Kettle" );
    Encr.init( passwordEncoderPluginID );
  }

  public static List<String> getDefaultAttributes() {
    return Arrays.asList( "targetURL", "username", "password", "timeout", "compression", "module" );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testBaseCheck() {
    SalesforceStepMeta meta = mock( SalesforceStepMeta.class, Mockito.CALLS_REAL_METHODS );
    meta.setDefault();
    List<CheckResultInterface> remarks = new ArrayList<CheckResultInterface>();
    meta.check( remarks, null, null, null, null, null, null, null, null, null );
    boolean hasError = false;
    for ( CheckResultInterface cr : remarks ) {
      if ( cr.getType() == CheckResult.TYPE_RESULT_ERROR ) {
        hasError = true;
      }
    }
    assertFalse( remarks.isEmpty() );
    assertTrue( hasError );
    remarks.clear();

    meta.setDefault();
    meta.setUsername( "anonymous" );
    meta.check( remarks, null, null, null, null, null, null, null, null, null );
    hasError = false;
    for ( CheckResultInterface cr : remarks ) {
      if ( cr.getType() == CheckResult.TYPE_RESULT_ERROR ) {
        hasError = true;
      }
    }
    assertFalse( remarks.isEmpty() );
    assertFalse( hasError );
    remarks.clear();

    meta.setDefault();
    meta.setTargetURL( null );
    meta.setUserName( "anonymous" );
    meta.setPassword( "password" );
    meta.check( remarks, null, null, null, null, null, null, null, null, null );
    hasError = false;
    for ( CheckResultInterface cr : remarks ) {
      if ( cr.getType() == CheckResult.TYPE_RESULT_ERROR ) {
        hasError = true;
      }
    }
    assertFalse( remarks.isEmpty() );
    assertTrue( hasError );
    remarks.clear();

    meta.setDefault();
    meta.setUsername( "anonymous" );
    meta.setModule( null );
    meta.check( remarks, null, null, null, null, null, null, null, null, null );
    hasError = false;
    for ( CheckResultInterface cr : remarks ) {
      if ( cr.getType() == CheckResult.TYPE_RESULT_ERROR ) {
        hasError = true;
      }
    }
    assertFalse( remarks.isEmpty() );
    assertTrue( hasError );
  }
}
