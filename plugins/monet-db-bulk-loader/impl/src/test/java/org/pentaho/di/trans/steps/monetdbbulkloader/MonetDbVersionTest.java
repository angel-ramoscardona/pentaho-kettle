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

package org.pentaho.di.trans.steps.monetdbbulkloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Tatsiana_Kasiankova
 * 
 */
public class MonetDbVersionTest {

  private MonetDbVersion monetDbVersion;

  @Before
  public void setup() throws Exception {
    monetDbVersion = null;
  }

  @Test
  public void testDbversionCreatedFromString() throws Exception {
    String dbVersion = "1.2.3";
    monetDbVersion = new MonetDbVersion( dbVersion );
    assertNotNull( monetDbVersion.getMajorVersion() );
    assertEquals( Integer.valueOf( 1 ), monetDbVersion.getMajorVersion() );
    assertNotNull( monetDbVersion.getMinorVersion() );
    assertEquals( Integer.valueOf( 2 ), monetDbVersion.getMinorVersion() );
    assertNotNull( monetDbVersion.getPatchVersion() );
    assertEquals( Integer.valueOf( 3 ), monetDbVersion.getPatchVersion() );
  }

  @Test
  public void testDbversionCreated() throws Exception {
    monetDbVersion = new MonetDbVersion( 1, 2, 3 );
    assertNotNull( monetDbVersion.getMajorVersion() );
    assertEquals( Integer.valueOf( 1 ), monetDbVersion.getMajorVersion() );
    assertNotNull( monetDbVersion.getMinorVersion() );
    assertEquals( Integer.valueOf( 2 ), monetDbVersion.getMinorVersion() );
    assertNotNull( monetDbVersion.getPatchVersion() );
    assertEquals( Integer.valueOf( 3 ), monetDbVersion.getPatchVersion() );
  }

  @Test
  public void testIllegalArgumentExceptionThrows_IfDbVersionNull() {
    try {
      monetDbVersion = new MonetDbVersion( null );
      fail( "Should throw MonetDbVersionException but it does not. " );
    } catch ( MonetDbVersionException ex ) {
      assertTrue( ex.getLocalizedMessage().contains( "DB Version can not be null." ) );
    }
  }

  @Test
  public void testIllegalArgumentExceptionThrows_IfDbVersionNotMatchesVersionPattern() {
    String dbVersion = "1.8.d";
    try {
      monetDbVersion = new MonetDbVersion( dbVersion );
      fail( "Should throw MonetDbVersionException but it does not. " );
    } catch ( MonetDbVersionException ex ) {
      assertTrue( ex.getLocalizedMessage().contains( "DB Version format is invalid: " + dbVersion ) );
    }
  }

  @Test
  public void testDbVersionWithouPatchVersion() throws Exception {

    String dbVersion = "785.2";
    monetDbVersion = new MonetDbVersion( dbVersion );
    assertNotNull( monetDbVersion.getMajorVersion() );
    assertEquals( Integer.valueOf( 785 ), monetDbVersion.getMajorVersion() );
    assertNotNull( monetDbVersion.getMinorVersion() );
    assertEquals( Integer.valueOf( 2 ), monetDbVersion.getMinorVersion() );
    assertNull( monetDbVersion.getPatchVersion() );

  }

  @Test
  public void testCompareVersions_DiffInPatch() throws Exception {

    String dbVersionBigger = "785.2.3";
    String dbVersion = "785.2.2";
    assertEquals( 1, new MonetDbVersion( dbVersionBigger ).compareTo( new MonetDbVersion( dbVersion ) ) );
  }

  @Test
  public void testCompareVersions_DiffInMinor() throws Exception {

    String dbVersionBigger = "785.5.3";
    String dbVersion = "785.2.2";
    assertEquals( 1, new MonetDbVersion( dbVersionBigger ).compareTo( new MonetDbVersion( dbVersion ) ) );
  }

  @Test
  public void testCompareVersions_DiffInMajor() throws Exception {

    String dbVersionBigger = "786.5.3";
    String dbVersion = "785.2.2";
    assertEquals( 1, new MonetDbVersion( dbVersionBigger ).compareTo( new MonetDbVersion( dbVersion ) ) );
  }

  @Test
  public void testCompareVersions_DiffInMajor_LongVersion() throws Exception {

    String dbVersionBigger = "788.5.3.8.9.7.5";
    String dbVersion = "785.2.2";
    assertEquals( 1, new MonetDbVersion( dbVersionBigger ).compareTo( new MonetDbVersion( dbVersion ) ) );
  }

  @Test
  public void testCompareVersions_TheSame() throws Exception {

    String dbVersionBigger = "11.11.7";
    String dbVersion = "11.11.7";
    assertEquals( 0, new MonetDbVersion( dbVersionBigger ).compareTo( new MonetDbVersion( dbVersion ) ) );
  }

  @Test
  public void testCompareVersions_NoPatch() throws Exception {

    String dbVersionBigger = "11.18";
    String dbVersion = "11.17.17";
    assertEquals( 1, new MonetDbVersion( dbVersionBigger ).compareTo( new MonetDbVersion( dbVersion ) ) );
  }

}
