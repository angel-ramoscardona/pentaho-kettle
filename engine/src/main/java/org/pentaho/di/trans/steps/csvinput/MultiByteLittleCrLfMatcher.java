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


package org.pentaho.di.trans.steps.csvinput;

public class MultiByteLittleCrLfMatcher implements CrLfMatcherInterface {

  @Override
  public boolean isReturn( byte[] source, int location ) {
    if ( location >= 1 ) {
      return source[location] == 0x0d && source[location + 1] == 0x00;
    } else {
      return false;
    }
  }

  @Override
  public boolean isLineFeed( byte[] source, int location ) {
    if ( location >= 1 ) {
      return source[location] == 0x0a && source[location + 1] == 0x00;
    } else {
      return false;
    }
  }

}
