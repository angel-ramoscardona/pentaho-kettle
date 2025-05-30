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

package org.pentaho.repositoryvfs.vfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.RandomAccessContent;
import org.apache.commons.vfs2.util.RandomAccessMode;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.repository2.unified.fileio.RepositoryFileInputStream;
import org.pentaho.platform.repository2.unified.fileio.RepositoryFileOutputStream;

public class RepositoryVfsFileContent implements FileContent {
  private final RepositoryVfsFileObject source;

  public RepositoryVfsFileContent( RepositoryVfsFileObject source ) {
    this.source = source;
  }

  @Override
  public FileObject getFile() {
    throw new NotImplementedException();
  }

  @Override
  public long getSize() throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public long getLastModifiedTime() throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public void setLastModifiedTime( long modTime ) throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public boolean hasAttribute( String attrName ) throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public Map<String, Object> getAttributes() throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public String[] getAttributeNames() throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public Object getAttribute( String attrName ) throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public void setAttribute( String attrName, Object value ) throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public void removeAttribute( String attrName ) throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public Certificate[] getCertificates() throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public InputStream getInputStream() throws FileSystemException {
    IUnifiedRepository repo = source.provider.getRepo().getUnderlyingRepository();
    try {
      return new RepositoryFileInputStream( source.path, repo );
    } catch ( FileNotFoundException ex ) {
      return null;
    }
  }

  @Override
  public OutputStream getOutputStream() throws FileSystemException {
    IUnifiedRepository repo = source.provider.getRepo().getUnderlyingRepository();
    return new RepositoryFileOutputStream( source.path, false, false, repo, false );
  }

  @Override
  public RandomAccessContent getRandomAccessContent( RandomAccessMode mode ) throws FileSystemException {
    throw new NotImplementedException( "Random access to file in repository is not possible" );
  }

  @Override
  public OutputStream getOutputStream( boolean bAppend ) throws FileSystemException {
    if ( bAppend ) {
      throw new NotImplementedException( "Append file in repository is not possible" );
    } else {
      return getOutputStream();
    }
  }

  @Override
  public void close() throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public FileContentInfo getContentInfo() throws FileSystemException {
    throw new NotImplementedException();
  }

  @Override
  public boolean isOpen() {
    throw new NotImplementedException();
  }

  @Override
  public long write( FileContent output ) throws IOException {
    throw new NotImplementedException();
  }

  @Override
  public long write( FileObject file ) throws IOException {
    throw new NotImplementedException();
  }

  @Override
  public long write( OutputStream output ) throws IOException {
    throw new NotImplementedException();
  }

  @Override
  public long write( OutputStream output, int bufferSize ) throws IOException {
    throw new NotImplementedException();
  }
}
