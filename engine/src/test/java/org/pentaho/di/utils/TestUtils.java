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


package org.pentaho.di.utils;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.bowl.DefaultBowl;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;

public class TestUtils {


  /**
   * Do not use this method because it does not delete the temp folder after java process tear down
   */
  @Deprecated
  public static String createTempDir() {
    String ret = null;
    try {
      /*
       * Java.io.File only creates Temp files, so repurpose the filename for a temporary folder
       * Delete the file that's created, and re-create as a folder. 
       */
      File file = File.createTempFile( "temp_pentaho_test_dir", String.valueOf( System.currentTimeMillis() ) );
      file.delete();
      file.mkdir();
      file.deleteOnExit();
      ret = file.getAbsolutePath();
    } catch ( Exception ex ) {
      System.out.println( "Can't create temp folder" );
      ex.printStackTrace();
    }
    return ret;
  }

  public static File getInputFile( String prefix, String suffix ) throws IOException {
    File inputFile = File.createTempFile( prefix, suffix );
    inputFile.deleteOnExit();
    FileUtils.writeStringToFile( inputFile, UUID.randomUUID().toString(), "UTF-8" );
    return inputFile;
  }

  public static String createRamFile( String path ) {
    return createRamFile( path, null );
  }

  public static String createRamFile( String path, VariableSpace space ) {
    if ( space == null ) {
      space = new Variables();
      space.initializeVariablesFrom( null );
    }
    try {
      FileObject file = KettleVFS.getInstance( DefaultBowl.getInstance() ).getFileObject( "ram://" + path, space );
      file.createFile();
      return file.getName().getURI();
    } catch ( FileSystemException | KettleFileException e ) {
      throw new RuntimeException( e );
    }
  }

  public static FileObject getFileObject( String vfsPath ) {
    return getFileObject( vfsPath, null );
  }

  public static FileObject getFileObject( String vfsPath, VariableSpace space ) {
    if ( space == null ) {
      space = new Variables();
      space.initializeVariablesFrom( null );
    }
    try {
      return KettleVFS.getInstance( DefaultBowl.getInstance() ).getFileObject( vfsPath, space );
    } catch ( KettleFileException e ) {
      throw new RuntimeException( e );
    }
  }

  public static String toUnixLineSeparators( String string ) {
    if ( string != null ) {
      string = string.replaceAll( "\r", "" );
    }
    return string;
  }

  public static void checkEqualsHashCodeConsistency( Object object1, Object object2 ) {
    if ( object1.equals( object2 ) ) {
      assertTrue( "inconsistent hashcode and equals", object1.hashCode() == object2.hashCode() );
    }
  }
}
