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


package org.pentaho.di.job.entries.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pentaho.di.core.bowl.DefaultBowl;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.vfs.KettleVFS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;

import static org.junit.Assert.assertTrue;

/**
 * @author Andrey Khayrutdinov
 */
public class SFTPClientIT {

  @ClassRule
  public static TemporaryFolder folder = new TemporaryFolder();

  private static SftpServer server;

  @BeforeClass
  public static void startServer() throws Exception {
    KettleEnvironment.init();

    server = SftpServer.createDefaultServer( folder );
    server.start();
  }

  @AfterClass
  public static void stopServer() throws Exception {
    server.stop();
    server = null;
  }


  private SFTPClient client;
  private Session session;
  private ChannelSftp channel;

  @Before
  public void setUp() throws Exception {
    session = server.createJschSession();
    session.connect();

    client = new SFTPClient( DefaultBowl.getInstance(), InetAddress.getByName( "localhost" ), server.getPort(),
      server.getUsername() );
    client.login( server.getPassword() );

    channel = (ChannelSftp) session.openChannel( "sftp" );
  }

  @After
  public void tearDown() throws Exception {
    if ( channel != null && channel.isConnected() ) {
      channel.disconnect();
    }
    channel = null;

    if ( session.isConnected() ) {
      session.disconnect();
    }
    session = null;
  }

  @Test
  public void putFile() throws Exception {
    final byte[] data = "putFile()".getBytes();

    client.put( new ByteArrayInputStream( data ), "uploaded.txt" );

    ByteArrayOutputStream uploaded = new ByteArrayOutputStream();
    channel.connect();
    try ( InputStream inputStream = channel.get( "uploaded.txt" ) ) {
      IOUtils.copy( inputStream, uploaded );
    }

    assertTrue(
      IOUtils.contentEquals( new ByteArrayInputStream( data ), new ByteArrayInputStream( uploaded.toByteArray() ) ) );
  }

  @Test
  public void getFile() throws Exception {
    final byte[] data = "getFile()".getBytes();

    channel.connect();
    channel.put( new ByteArrayInputStream( data ), "downloaded.txt" );

    client.get( DefaultBowl.getInstance(), KettleVFS.getInstance( DefaultBowl.getInstance() )
      .getFileObject( "ram://downloaded.txt" ), "downloaded.txt" );

    FileObject downloaded = KettleVFS.getInstance( DefaultBowl.getInstance() ).getFileObject( "ram://downloaded.txt" );
    assertTrue( downloaded.exists() );
    assertTrue( IOUtils.contentEquals( downloaded.getContent().getInputStream(), new ByteArrayInputStream( data ) ) );
  }
}
