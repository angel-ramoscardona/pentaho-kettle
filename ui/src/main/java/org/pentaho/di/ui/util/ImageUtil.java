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


/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.pentaho.di.ui.util;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.core.bowl.DefaultBowl;
import org.pentaho.di.core.vfs.KettleVFS;

public class ImageUtil {
  private static FileObject base;

  static {
    try {
      base = KettleVFS.getInstance().getFileSystemManager().resolveFile( System.getProperty( "user.dir" ) );
    } catch ( FileSystemException e ) {
      e.printStackTrace();
      base = null;
    }
  }

  public static Image makeImageTransparent( Display display, Image tempImage, RGB transparentColor ) {
    ImageData imageData = tempImage.getImageData();
    int pixelIndex = imageData.palette.getPixel( transparentColor );
    imageData.transparentPixel = pixelIndex;
    Image image = new Image( display, imageData );
    tempImage.dispose();
    return image;
  }

  public static InputStream getImageInputStream( Display display, String location ) {
    // assume the classloader for the active thread
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if ( cl == null ) {
      // Can't count on Thread.currentThread().getContextClassLoader() being non-null on Mac
      // Have to provide some fallback
      cl = ImageUtil.class.getClassLoader();
    }
    URL res = cl.getResource( location );
    if ( res != null ) {
      try {
        java.io.InputStream s = res.openStream();
        return s;
      } catch ( IOException e ) {
        // do nothing. just move on to trying to load via file system
      }
    }
    try {
      FileObject imageFileObject = KettleVFS.getInstance().getFileSystemManager().resolveFile( base, location );
      return KettleVFS.getInputStream( imageFileObject );
    } catch ( FileSystemException e ) {
      throw new RuntimeException( "Unable to load image with name [" + location + "]", e );
    }
  }

  /**
   * TODO: Load splash and common images.
   */
  public static Image getImageAsResource( Display display, String location ) {
    return new Image( display, getImageInputStream( display, location ) );
  }

  /**
   * TODO: not used.
   */
  public static Image getImage( Display display, Class<?> resourceClass, String filename ) {
    try {
      return new Image( display, resourceClass.getResourceAsStream( filename ) );
    } catch ( Exception e ) {
      try {
        return new Image( display, resourceClass.getResourceAsStream( "/" + filename ) );
      } catch ( Exception e2 ) {
        return getImage( display, filename );
      }
    }
  }

  /**
   * TODO: Load job and step images.
   */
  public static Image getImage( Display display, ClassLoader classLoader, String filename ) {
    try {
      return new Image( display, classLoader.getResourceAsStream( filename ) );
    } catch ( Exception e ) {
      try {
        return new Image( display, classLoader.getResourceAsStream( "/" + filename ) );
      } catch ( Exception e2 ) {
        return getImage( display, filename );
      }
    }
  }

  /**
   * TODO: GUI resources.
   */
  public static Image getImage( Display display, String location ) {
    // TODO: find other instances of getImage (plugin, steps) and transition them to new model through an laf manager
    try {
      InputStream is = KettleVFS.getInstance( DefaultBowl.getInstance() ).getInputStream( location );
      Image im = new Image( display, is );
      is.close();
      return im;
    } catch ( Exception e ) {
      try {
        return new Image( display, ImageUtil.class.getClassLoader().getResourceAsStream( location ) );
      } catch ( Exception npe ) {
        throw new RuntimeException( "Unable to load image with name [" + location + "]", e );
      }
    }

  }

  public static ImageData convertToSWT( BufferedImage bufferedImage ) {
    if ( bufferedImage.getColorModel() instanceof DirectColorModel ) {
      DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
      PaletteData palette =
        new PaletteData( colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask() );
      ImageData data =
        new ImageData( bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette );
      WritableRaster raster = bufferedImage.getRaster();
      int[] pixelArray = new int[4];
      for ( int y = 0; y < data.height; y++ ) {
        for ( int x = 0; x < data.width; x++ ) {
          raster.getPixel( x, y, pixelArray );
          int pixel = palette.getPixel( new RGB( pixelArray[0], pixelArray[1], pixelArray[2] ) );
          data.setPixel( x, y, pixel );
          data.setAlpha( x, y, pixelArray[3] );
        }
      }
      return data;
    } else if ( bufferedImage.getColorModel() instanceof IndexColorModel ) {
      IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
      int size = colorModel.getMapSize();
      byte[] reds = new byte[size];
      byte[] greens = new byte[size];
      byte[] blues = new byte[size];
      colorModel.getReds( reds );
      colorModel.getGreens( greens );
      colorModel.getBlues( blues );
      RGB[] rgbs = new RGB[size];
      for ( int i = 0; i < rgbs.length; i++ ) {
        rgbs[i] = new RGB( reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF );
      }
      PaletteData palette = new PaletteData( rgbs );
      ImageData data =
        new ImageData( bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette );
      data.transparentPixel = colorModel.getTransparentPixel();
      WritableRaster raster = bufferedImage.getRaster();
      int[] pixelArray = new int[1];
      for ( int y = 0; y < data.height; y++ ) {
        for ( int x = 0; x < data.width; x++ ) {
          raster.getPixel( x, y, pixelArray );
          data.setPixel( x, y, pixelArray[0] );
        }
      }
      return data;
    }
    return null;
  }

}
