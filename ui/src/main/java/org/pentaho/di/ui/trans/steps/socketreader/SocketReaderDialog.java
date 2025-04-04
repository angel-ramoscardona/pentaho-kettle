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


package org.pentaho.di.ui.trans.steps.socketreader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.socketreader.SocketReaderMeta;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.ui.trans.step.ComponentSelectionListener;

public class SocketReaderDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = SocketReaderMeta.class; // for i18n purposes, needed by Translator2!!

  private SocketReaderMeta input;
  private TextVar wHostname;
  private TextVar wPort;
  private TextVar wBufferSize;
  private Button wCompressed;

  public SocketReaderDialog( Shell parent, Object in, TransMeta tr, String sname ) {
    super( parent, (BaseStepMeta) in, tr, sname );
    input = (SocketReaderMeta) in;
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };
    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "SocketReaderDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Stepname line
    wlStepname = new Label( shell, SWT.RIGHT );
    wlStepname.setText( BaseMessages.getString( PKG, "SocketReaderDialog.Stepname.Label" ) );
    props.setLook( wlStepname );
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment( 0, 0 );
    fdlStepname.right = new FormAttachment( middle, -margin );
    fdlStepname.top = new FormAttachment( 0, margin );
    wlStepname.setLayoutData( fdlStepname );
    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepname.setText( stepname );
    props.setLook( wStepname );
    wStepname.addModifyListener( lsMod );
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment( middle, 0 );
    fdStepname.top = new FormAttachment( 0, margin );
    fdStepname.right = new FormAttachment( 100, 0 );
    wStepname.setLayoutData( fdStepname );

    // Hostname line
    Label wlHostname = new Label( shell, SWT.RIGHT );
    wlHostname.setText( BaseMessages.getString( PKG, "SocketReaderDialog.Hostname.Label" ) );
    props.setLook( wlHostname );
    FormData fdlHostname = new FormData();
    fdlHostname.left = new FormAttachment( 0, 0 );
    fdlHostname.right = new FormAttachment( middle, -margin );
    fdlHostname.top = new FormAttachment( wStepname, margin );
    wlHostname.setLayoutData( fdlHostname );
    wHostname = new TextVar( transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wHostname.setText( stepname );
    props.setLook( wHostname );
    wHostname.addModifyListener( lsMod );
    FormData fdHostname = new FormData();
    fdHostname.left = new FormAttachment( middle, 0 );
    fdHostname.top = new FormAttachment( wStepname, margin );
    fdHostname.right = new FormAttachment( 100, 0 );
    wHostname.setLayoutData( fdHostname );

    // Port line
    Label wlPort = new Label( shell, SWT.RIGHT );
    wlPort.setText( BaseMessages.getString( PKG, "SocketReaderDialog.Port.Label" ) );
    props.setLook( wlPort );
    FormData fdlPort = new FormData();
    fdlPort.left = new FormAttachment( 0, 0 );
    fdlPort.right = new FormAttachment( middle, -margin );
    fdlPort.top = new FormAttachment( wHostname, margin );
    wlPort.setLayoutData( fdlPort );
    wPort = new TextVar( transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wPort.setText( stepname );
    props.setLook( wPort );
    wPort.addModifyListener( lsMod );
    FormData fdPort = new FormData();
    fdPort.left = new FormAttachment( middle, 0 );
    fdPort.top = new FormAttachment( wHostname, margin );
    fdPort.right = new FormAttachment( 100, 0 );
    wPort.setLayoutData( fdPort );

    // BufferSize line
    Label wlBufferSize = new Label( shell, SWT.RIGHT );
    wlBufferSize.setText( BaseMessages.getString( PKG, "SocketReaderDialog.BufferSize.Label" ) );
    props.setLook( wlBufferSize );
    FormData fdlBufferSize = new FormData();
    fdlBufferSize.left = new FormAttachment( 0, 0 );
    fdlBufferSize.right = new FormAttachment( middle, -margin );
    fdlBufferSize.top = new FormAttachment( wPort, margin );
    wlBufferSize.setLayoutData( fdlBufferSize );
    wBufferSize = new TextVar( transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wBufferSize.setText( stepname );
    props.setLook( wBufferSize );
    wBufferSize.addModifyListener( lsMod );
    FormData fdBufferSize = new FormData();
    fdBufferSize.left = new FormAttachment( middle, 0 );
    fdBufferSize.top = new FormAttachment( wPort, margin );
    fdBufferSize.right = new FormAttachment( 100, 0 );
    wBufferSize.setLayoutData( fdBufferSize );

    // Compress socket data?
    Label wlCompressed = new Label( shell, SWT.RIGHT );
    props.setLook( wlCompressed );
    wlCompressed.setText( BaseMessages.getString( PKG, "SocketReaderDialog.Compressed.Label" ) );
    FormData fdlCompressed = new FormData();
    fdlCompressed.top = new FormAttachment( wBufferSize, margin );
    fdlCompressed.left = new FormAttachment( 0, 0 ); // First one in the left top corner
    fdlCompressed.right = new FormAttachment( middle, 0 );
    wlCompressed.setLayoutData( fdlCompressed );
    wCompressed = new Button( shell, SWT.CHECK );
    props.setLook( wCompressed );
    FormData fdCompressed = new FormData();
    fdCompressed.top = new FormAttachment( wBufferSize, margin );
    fdCompressed.left = new FormAttachment( middle, margin ); // To the right of the label
    fdCompressed.right = new FormAttachment( 95, 0 );
    wCompressed.setLayoutData( fdCompressed );
    wCompressed.addSelectionListener( new ComponentSelectionListener( input ) );

    // Some buttons
    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOK, wCancel }, margin, wCompressed );

    // Add listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wCancel.addListener( SWT.Selection, lsCancel );
    wOK.addListener( SWT.Selection, lsOK );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wStepname.addSelectionListener( lsDef );
    wHostname.addSelectionListener( lsDef );
    wPort.addSelectionListener( lsDef );
    wBufferSize.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    if ( Utils.isEmpty( wStepname.getText() ) ) {
      return;
    }

    wHostname.setText( Const.NVL( input.getHostname(), "" ) );
    wPort.setText( Const.NVL( input.getPort(), "" ) );
    wBufferSize.setText( Const.NVL( input.getBufferSize(), "" ) );
    wCompressed.setSelection( input.isCompressed() );

    wStepname.selectAll();
    wStepname.setFocus();
  }

  private void cancel() {
    stepname = null;
    input.setChanged( changed );
    dispose();
  }

  private void ok() {
    input.setHostname( wHostname.getText() );
    input.setPort( wPort.getText() );
    input.setBufferSize( wBufferSize.getText() );
    input.setCompressed( wCompressed.getSelection() );

    stepname = wStepname.getText(); // return value

    dispose();
  }
}
