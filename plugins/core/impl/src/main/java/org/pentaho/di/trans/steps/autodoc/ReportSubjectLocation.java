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


package org.pentaho.di.trans.steps.autodoc;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObjectType;

/**
 * A transformation or job location
 *
 * @author matt
 *
 */
public class ReportSubjectLocation {
  private String filename;
  private RepositoryDirectoryInterface directory;
  private String name;
  private RepositoryObjectType objectType;

  /**
   * @param filename
   *          the name of the file
   * @param directory
   *          the directory in the repository where the subject lives
   * @param name
   *          the name of the subject in the repository
   * @param objectType
   *          the object type to report on
   */
  public ReportSubjectLocation( String filename, RepositoryDirectoryInterface directory, String name,
    RepositoryObjectType objectType ) {
    this.filename = filename;
    this.directory = directory;
    this.name = name;
    this.objectType = objectType;
  }

  @Override
  public String toString() {
    if ( Utils.isEmpty( filename ) ) {
      String dir = directory.toString();
      if ( dir.endsWith( "/" ) ) {
        return dir + name + " (" + objectType.getTypeDescription() + ")";
      } else {
        return dir + "/" + name + " (" + objectType.getTypeDescription() + ")";
      }
    } else {
      return filename;
    }
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals( Object object ) {
    if ( !( object instanceof ReportSubjectLocation ) ) {
      return false;
    }
    if ( this == object ) {
      return true;
    }
    return toString().equals( ( (ReportSubjectLocation) object ).toString() );
  }

  /**
   * @return true if the report subject is a transformation.
   */
  public boolean isTransformation() {
    if ( objectType.equals( RepositoryObjectType.TRANSFORMATION ) ) {
      return true;
    }
    return false;
  }

  /**
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @param filename
   *          the filename to set
   */
  public void setFilename( String filename ) {
    this.filename = filename;
  }

  /**
   * @return the directory
   */
  public RepositoryDirectoryInterface getDirectory() {
    return directory;
  }

  /**
   * @param directory
   *          the directory to set
   */
  public void setDirectory( RepositoryDirectoryInterface directory ) {
    this.directory = directory;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * @return the objectType
   */
  public RepositoryObjectType getObjectType() {
    return objectType;
  }

  /**
   * @param objectType
   *          the objectType to set
   */
  public void setObjectType( RepositoryObjectType objectType ) {
    this.objectType = objectType;
  }

}
