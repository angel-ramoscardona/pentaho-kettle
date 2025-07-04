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


package org.pentaho.di.ui.job.entries.http;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.HTTPProtocol;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.http.JobEntryHTTP;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.events.dialog.FilterType;
import org.pentaho.di.ui.core.events.dialog.SelectionAdapterFileDialogTextVar;
import org.pentaho.di.ui.core.events.dialog.SelectionAdapterOptions;
import org.pentaho.di.ui.core.events.dialog.SelectionOperation;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.PasswordTextVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * This dialog allows you to edit the SQL job entry settings. (select the connection and the sql script to be executed)
 *
 * @author Matt
 * @since 19-06-2003
 */
public class JobEntryHTTPDialog extends JobEntryDialog implements JobEntryDialogInterface {
  private static Class<?> PKG = JobEntryHTTP.class; // for i18n purposes, needed by Translator2!!

  private static final String[] FILETYPES = new String[] { BaseMessages.getString( PKG, "JobHTTP.Filetype.All" ) };

  private Label wlName;

  private Text wName;

  private FormData fdlName, fdName;

  private Label wlURL;

  private TextVar wURL;

  private FormData fdlURL, fdURL;

  private Label wlRunEveryRow;

  private Button wRunEveryRow;

  private FormData fdlRunEveryRow, fdRunEveryRow;

  private Label wlFieldURL;

  private TextVar wFieldURL;

  private FormData fdlFieldURL, fdFieldURL;

  private Label wlFieldUpload;

  private TextVar wFieldUpload;

  private FormData fdlFieldUpload, fdFieldUpload;

  private Label wlFieldTarget;

  private TextVar wFieldTarget;

  private FormData fdlFieldTarget, fdFieldTarget;

  private Label wlTargetFile;

  private TextVar wTargetFile;

  private FormData fdlTargetFile, fdTargetFile;

  private Button wbTargetFile;

  private FormData fdbTargetFile;

  private Label wlAppend;

  private Button wAppend;

  private FormData fdlAppend, fdAppend;

  private Label wlDateTimeAdded;

  private Button wDateTimeAdded;

  private FormData fdlDateTimeAdded, fdDateTimeAdded;

  private Label wlTargetExt;

  private TextVar wTargetExt;

  private FormData fdlTargetExt, fdTargetExt;

  private Label wlUploadFile;

  private TextVar wUploadFile;

  private Button wbUploadFile;

  private FormData fdbUploadFile;

  private FormData fdlUploadFile, fdUploadFile;

  private Label wlUserName;

  private TextVar wUserName;

  private FormData fdlUserName, fdUserName;

  private Label wlPassword;

  private TextVar wPassword;

  private FormData fdlPassword, fdPassword;

  private Label wlProxyServer;

  private TextVar wProxyServer;

  private FormData fdlProxyServer, fdProxyServer;

  private Label wlProxyPort;

  private TextVar wProxyPort;

  private FormData fdlProxyPort, fdProxyPort;

  private Label wlNonProxyHosts;

  private TextVar wNonProxyHosts;

  private FormData fdlNonProxyHosts, fdNonProxyHosts;

  private TableView wHeaders;

  private FormData fdHeaders;

  private ColumnInfo[] colinf;

  private Button wOK, wCancel;

  private Listener lsOK, lsCancel;

  private Group wAuthentication, wUpLoadFile, wTargetFileGroup;
  private FormData fdAuthentication, fdUpLoadFile, fdTargetFileGroup;

  private Label wlAddFilenameToResult;
  private Button wAddFilenameToResult;
  private FormData fdlAddFilenameToResult, fdAddFilenameToResult;

  private JobEntryHTTP jobEntry;

  private Shell shell;

  private SelectionAdapter lsDef;

  private boolean changed;

  private CTabFolder wTabFolder;
  private Composite wGeneralComp, wHeadersComp;
  private CTabItem wGeneralTab, wHeadersTab;
  private FormData fdGeneralComp, fdHeadersComp;
  private FormData fdTabFolder;

  public JobEntryHTTPDialog( Shell parent, JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta ) {
    super( parent, jobEntryInt, rep, jobMeta );
    jobEntry = (JobEntryHTTP) jobEntryInt;
    if ( this.jobEntry.getName() == null ) {
      this.jobEntry.setName( BaseMessages.getString( PKG, "JobHTTP.Name.Default" ) );
    }
  }

  public JobEntryInterface open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, props.getJobsDialogStyle() );
    props.setLook( shell );
    JobDialog.setShellImage( shell, jobEntry );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        jobEntry.setChanged();
      }
    };
    changed = jobEntry.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "JobHTTP.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Job entry name line
    wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( BaseMessages.getString( PKG, "JobHTTP.Name.Label" ) );
    props.setLook( wlName );
    fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, -margin );
    fdlName.top = new FormAttachment( 0, margin );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wName );
    wName.addModifyListener( lsMod );
    fdName = new FormData();
    fdName.left = new FormAttachment( middle, 0 );
    fdName.top = new FormAttachment( 0, margin );
    fdName.right = new FormAttachment( 100, 0 );
    wName.setLayoutData( fdName );

    wTabFolder = new CTabFolder( shell, SWT.BORDER );
    props.setLook( wTabFolder, Props.WIDGET_STYLE_TAB );

    // ////////////////////////
    // START OF GENERAL TAB ///
    // ////////////////////////

    wGeneralTab = new CTabItem( wTabFolder, SWT.NONE );
    wGeneralTab.setText( BaseMessages.getString( PKG, "JobHTTP.Tab.General.Label" ) );
    wGeneralComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wGeneralComp );
    FormLayout generalLayout = new FormLayout();
    generalLayout.marginWidth = 3;
    generalLayout.marginHeight = 3;
    wGeneralComp.setLayout( generalLayout );

    // URL line
    wlURL = new Label( wGeneralComp, SWT.RIGHT );
    wlURL.setText( BaseMessages.getString( PKG, "JobHTTP.URL.Label" ) );
    props.setLook( wlURL );
    fdlURL = new FormData();
    fdlURL.left = new FormAttachment( 0, 0 );
    fdlURL.top = new FormAttachment( wName, 2 * margin );
    fdlURL.right = new FormAttachment( middle, -margin );
    wlURL.setLayoutData( fdlURL );
    wURL =
      new TextVar( jobMeta, wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER, BaseMessages.getString(
        PKG, "JobHTTP.URL.Tooltip" ) );
    props.setLook( wURL );
    wURL.addModifyListener( lsMod );
    fdURL = new FormData();
    fdURL.left = new FormAttachment( middle, 0 );
    fdURL.top = new FormAttachment( wName, 2 * margin );
    fdURL.right = new FormAttachment( 100, 0 );
    wURL.setLayoutData( fdURL );

    // RunEveryRow line
    wlRunEveryRow = new Label( wGeneralComp, SWT.RIGHT );
    wlRunEveryRow.setText( BaseMessages.getString( PKG, "JobHTTP.RunForEveryRow.Label" ) );
    props.setLook( wlRunEveryRow );
    fdlRunEveryRow = new FormData();
    fdlRunEveryRow.left = new FormAttachment( 0, 0 );
    fdlRunEveryRow.top = new FormAttachment( wURL, margin );
    fdlRunEveryRow.right = new FormAttachment( middle, -margin );
    wlRunEveryRow.setLayoutData( fdlRunEveryRow );
    wRunEveryRow = new Button( wGeneralComp, SWT.CHECK );
    wRunEveryRow.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.RunForEveryRow.Tooltip" ) );
    props.setLook( wRunEveryRow );
    fdRunEveryRow = new FormData();
    fdRunEveryRow.left = new FormAttachment( middle, 0 );
    fdRunEveryRow.top = new FormAttachment( wURL, margin );
    fdRunEveryRow.right = new FormAttachment( 100, 0 );
    wRunEveryRow.setLayoutData( fdRunEveryRow );
    wRunEveryRow.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        setFlags();
      }
    } );

    // FieldURL line
    wlFieldURL = new Label( wGeneralComp, SWT.RIGHT );
    wlFieldURL.setText( BaseMessages.getString( PKG, "JobHTTP.InputField.Label" ) );
    props.setLook( wlFieldURL );
    fdlFieldURL = new FormData();
    fdlFieldURL.left = new FormAttachment( 0, 0 );
    fdlFieldURL.top = new FormAttachment( wRunEveryRow, margin );
    fdlFieldURL.right = new FormAttachment( middle, -margin );
    wlFieldURL.setLayoutData( fdlFieldURL );
    wFieldURL = new TextVar( jobMeta, wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wFieldURL );
    wFieldURL.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.InputField.Tooltip" ) );
    wFieldURL.addModifyListener( lsMod );
    fdFieldURL = new FormData();
    fdFieldURL.left = new FormAttachment( middle, 0 );
    fdFieldURL.top = new FormAttachment( wRunEveryRow, margin );
    fdFieldURL.right = new FormAttachment( 100, 0 );
    wFieldURL.setLayoutData( fdFieldURL );

    // FieldUpload line

    wlFieldUpload = new Label( wGeneralComp, SWT.RIGHT );
    wlFieldUpload.setText( BaseMessages.getString( PKG, "JobHTTP.InputFieldUpload.Label" ) );
    props.setLook( wlFieldUpload );
    fdlFieldUpload = new FormData();
    fdlFieldUpload.left = new FormAttachment( 0, 0 );
    fdlFieldUpload.top = new FormAttachment( wFieldURL, margin );
    fdlFieldUpload.right = new FormAttachment( middle, -margin );
    wlFieldUpload.setLayoutData( fdlFieldUpload );
    wFieldUpload = new TextVar( jobMeta, wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wFieldUpload );
    wFieldUpload.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.InputFieldUpload.Tooltip" ) );
    wFieldUpload.addModifyListener( lsMod );
    fdFieldUpload = new FormData();
    fdFieldUpload.left = new FormAttachment( middle, 0 );
    fdFieldUpload.top = new FormAttachment( wFieldURL, margin );
    fdFieldUpload.right = new FormAttachment( 100, 0 );
    wFieldUpload.setLayoutData( fdFieldUpload );

    // FieldTarget line
    wlFieldTarget = new Label( wGeneralComp, SWT.RIGHT );
    wlFieldTarget.setText( BaseMessages.getString( PKG, "JobHTTP.InputFieldDest.Label" ) );
    props.setLook( wlFieldTarget );
    fdlFieldTarget = new FormData();
    fdlFieldTarget.left = new FormAttachment( 0, 0 );
    fdlFieldTarget.top = new FormAttachment( wFieldUpload, margin );
    fdlFieldTarget.right = new FormAttachment( middle, -margin );
    wlFieldTarget.setLayoutData( fdlFieldTarget );
    wFieldTarget = new TextVar( jobMeta, wGeneralComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wFieldTarget );
    wFieldTarget.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.InputFieldDest.Tooltip" ) );
    wFieldTarget.addModifyListener( lsMod );
    fdFieldTarget = new FormData();
    fdFieldTarget.left = new FormAttachment( middle, 0 );
    fdFieldTarget.top = new FormAttachment( wFieldUpload, margin );
    fdFieldTarget.right = new FormAttachment( 100, 0 );
    wFieldTarget.setLayoutData( fdFieldTarget );

    // ////////////////////////
    // START OF AuthenticationGROUP///
    // /
    wAuthentication = new Group( wGeneralComp, SWT.SHADOW_NONE );
    props.setLook( wAuthentication );
    wAuthentication.setText( BaseMessages.getString( PKG, "JobHTTP.Authentication.Group.Label" ) );

    FormLayout AuthenticationgroupLayout = new FormLayout();
    AuthenticationgroupLayout.marginWidth = 10;
    AuthenticationgroupLayout.marginHeight = 10;
    wAuthentication.setLayout( AuthenticationgroupLayout );

    // UserName line
    wlUserName = new Label( wAuthentication, SWT.RIGHT );
    wlUserName.setText( BaseMessages.getString( PKG, "JobHTTP.UploadUser.Label" ) );
    props.setLook( wlUserName );
    fdlUserName = new FormData();
    fdlUserName.left = new FormAttachment( 0, 0 );
    fdlUserName.top = new FormAttachment( wFieldTarget, margin );
    fdlUserName.right = new FormAttachment( middle, -margin );
    wlUserName.setLayoutData( fdlUserName );
    wUserName = new TextVar( jobMeta, wAuthentication, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wUserName );
    wUserName.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.UploadUser.Tooltip" ) );
    wUserName.addModifyListener( lsMod );
    fdUserName = new FormData();
    fdUserName.left = new FormAttachment( middle, 0 );
    fdUserName.top = new FormAttachment( wFieldTarget, margin );
    fdUserName.right = new FormAttachment( 100, 0 );
    wUserName.setLayoutData( fdUserName );

    // Password line
    wlPassword = new Label( wAuthentication, SWT.RIGHT );
    wlPassword.setText( BaseMessages.getString( PKG, "JobHTTP.UploadPassword.Label" ) );
    props.setLook( wlPassword );
    fdlPassword = new FormData();
    fdlPassword.left = new FormAttachment( 0, 0 );
    fdlPassword.top = new FormAttachment( wUserName, margin );
    fdlPassword.right = new FormAttachment( middle, -margin );
    wlPassword.setLayoutData( fdlPassword );
    wPassword = new PasswordTextVar( jobMeta, wAuthentication, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wPassword );
    wPassword.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.UploadPassword.Tooltip" ) );
    wPassword.addModifyListener( lsMod );
    fdPassword = new FormData();
    fdPassword.left = new FormAttachment( middle, 0 );
    fdPassword.top = new FormAttachment( wUserName, margin );
    fdPassword.right = new FormAttachment( 100, 0 );
    wPassword.setLayoutData( fdPassword );

    // ProxyServer line
    wlProxyServer = new Label( wAuthentication, SWT.RIGHT );
    wlProxyServer.setText( BaseMessages.getString( PKG, "JobHTTP.ProxyHost.Label" ) );
    props.setLook( wlProxyServer );
    fdlProxyServer = new FormData();
    fdlProxyServer.left = new FormAttachment( 0, 0 );
    fdlProxyServer.top = new FormAttachment( wPassword, 3 * margin );
    fdlProxyServer.right = new FormAttachment( middle, -margin );
    wlProxyServer.setLayoutData( fdlProxyServer );
    wProxyServer = new TextVar( jobMeta, wAuthentication, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wProxyServer );
    wProxyServer.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.ProxyHost.Tooltip" ) );
    wProxyServer.addModifyListener( lsMod );
    fdProxyServer = new FormData();
    fdProxyServer.left = new FormAttachment( middle, 0 );
    fdProxyServer.top = new FormAttachment( wPassword, 3 * margin );
    fdProxyServer.right = new FormAttachment( 100, 0 );
    wProxyServer.setLayoutData( fdProxyServer );

    // ProxyPort line
    wlProxyPort = new Label( wAuthentication, SWT.RIGHT );
    wlProxyPort.setText( BaseMessages.getString( PKG, "JobHTTP.ProxyPort.Label" ) );
    props.setLook( wlProxyPort );
    fdlProxyPort = new FormData();
    fdlProxyPort.left = new FormAttachment( 0, 0 );
    fdlProxyPort.top = new FormAttachment( wProxyServer, margin );
    fdlProxyPort.right = new FormAttachment( middle, -margin );
    wlProxyPort.setLayoutData( fdlProxyPort );
    wProxyPort = new TextVar( jobMeta, wAuthentication, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wProxyPort );
    wProxyPort.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.ProxyPort.Tooltip" ) );
    wProxyPort.addModifyListener( lsMod );
    fdProxyPort = new FormData();
    fdProxyPort.left = new FormAttachment( middle, 0 );
    fdProxyPort.top = new FormAttachment( wProxyServer, margin );
    fdProxyPort.right = new FormAttachment( 100, 0 );
    wProxyPort.setLayoutData( fdProxyPort );

    // IgnoreHosts line
    wlNonProxyHosts = new Label( wAuthentication, SWT.RIGHT );
    wlNonProxyHosts.setText( BaseMessages.getString( PKG, "JobHTTP.ProxyIgnoreRegexp.Label" ) );
    props.setLook( wlNonProxyHosts );
    fdlNonProxyHosts = new FormData();
    fdlNonProxyHosts.left = new FormAttachment( 0, 0 );
    fdlNonProxyHosts.top = new FormAttachment( wProxyPort, margin );
    fdlNonProxyHosts.right = new FormAttachment( middle, -margin );
    wlNonProxyHosts.setLayoutData( fdlNonProxyHosts );
    wNonProxyHosts = new TextVar( jobMeta, wAuthentication, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wNonProxyHosts );
    wNonProxyHosts.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.ProxyIgnoreRegexp.Tooltip" ) );
    wNonProxyHosts.addModifyListener( lsMod );
    fdNonProxyHosts = new FormData();
    fdNonProxyHosts.left = new FormAttachment( middle, 0 );
    fdNonProxyHosts.top = new FormAttachment( wProxyPort, margin );
    fdNonProxyHosts.right = new FormAttachment( 100, 0 );
    wNonProxyHosts.setLayoutData( fdNonProxyHosts );

    fdAuthentication = new FormData();
    fdAuthentication.left = new FormAttachment( 0, margin );
    fdAuthentication.top = new FormAttachment( wFieldTarget, margin );
    fdAuthentication.right = new FormAttachment( 100, -margin );
    wAuthentication.setLayoutData( fdAuthentication );
    // ///////////////////////////////////////////////////////////
    // / END OF AuthenticationGROUP GROUP
    // ///////////////////////////////////////////////////////////

    // ////////////////////////
    // START OF UpLoadFileGROUP///
    // /
    wUpLoadFile = new Group( wGeneralComp, SWT.SHADOW_NONE );
    props.setLook( wUpLoadFile );
    wUpLoadFile.setText( BaseMessages.getString( PKG, "JobHTTP.UpLoadFile.Group.Label" ) );

    FormLayout UpLoadFilegroupLayout = new FormLayout();
    UpLoadFilegroupLayout.marginWidth = 10;
    UpLoadFilegroupLayout.marginHeight = 10;
    wUpLoadFile.setLayout( UpLoadFilegroupLayout );

    // UploadFile line
    wlUploadFile = new Label( wUpLoadFile, SWT.RIGHT );
    wlUploadFile.setText( BaseMessages.getString( PKG, "JobHTTP.UploadFile.Label" ) );
    props.setLook( wlUploadFile );
    fdlUploadFile = new FormData();
    fdlUploadFile.left = new FormAttachment( 0, 0 );
    fdlUploadFile.top = new FormAttachment( wAuthentication, margin );
    fdlUploadFile.right = new FormAttachment( middle, -margin );
    wlUploadFile.setLayoutData( fdlUploadFile );

    wbUploadFile = new Button( wUpLoadFile, SWT.PUSH | SWT.CENTER );
    props.setLook( wbUploadFile );
    wbUploadFile.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    fdbUploadFile = new FormData();
    fdbUploadFile.right = new FormAttachment( 100, 0 );
    fdbUploadFile.top = new FormAttachment( wAuthentication, margin );
    wbUploadFile.setLayoutData( fdbUploadFile );

    wUploadFile = new TextVar( jobMeta, wUpLoadFile, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wUploadFile );
    wUploadFile.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.UploadFile.Tooltip" ) );
    wUploadFile.addModifyListener( lsMod );
    fdUploadFile = new FormData();
    fdUploadFile.left = new FormAttachment( middle, 0 );
    fdUploadFile.top = new FormAttachment( wAuthentication, margin );
    fdUploadFile.right = new FormAttachment( wbUploadFile, -margin );
    wUploadFile.setLayoutData( fdUploadFile );

    // Whenever something changes, set the tooltip to the expanded version:
    wUploadFile.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wUploadFile.setToolTipText( jobMeta.environmentSubstitute( wUploadFile.getText() ) );
      }
    } );

    wbUploadFile.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wUploadFile, jobMeta,
      new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.FILE,
        new FilterType[] { FilterType.ALL }, FilterType.ALL  ) ) );

    fdUpLoadFile = new FormData();
    fdUpLoadFile.left = new FormAttachment( 0, margin );
    fdUpLoadFile.top = new FormAttachment( wAuthentication, margin );
    fdUpLoadFile.right = new FormAttachment( 100, -margin );
    wUpLoadFile.setLayoutData( fdUpLoadFile );
    // ///////////////////////////////////////////////////////////
    // / END OF UpLoadFileGROUP GROUP
    // ///////////////////////////////////////////////////////////

    // ////////////////////////
    // START OF TargetFileGroupGROUP///
    // /
    wTargetFileGroup = new Group( wGeneralComp, SWT.SHADOW_NONE );
    props.setLook( wTargetFileGroup );
    wTargetFileGroup.setText( BaseMessages.getString( PKG, "JobHTTP.TargetFileGroup.Group.Label" ) );

    FormLayout TargetFileGroupgroupLayout = new FormLayout();
    TargetFileGroupgroupLayout.marginWidth = 10;
    TargetFileGroupgroupLayout.marginHeight = 10;
    wTargetFileGroup.setLayout( TargetFileGroupgroupLayout );

    // TargetFile line
    wlTargetFile = new Label( wTargetFileGroup, SWT.RIGHT );
    wlTargetFile.setText( BaseMessages.getString( PKG, "JobHTTP.TargetFile.Label" ) );
    props.setLook( wlTargetFile );
    fdlTargetFile = new FormData();
    fdlTargetFile.left = new FormAttachment( 0, 0 );
    fdlTargetFile.top = new FormAttachment( wUploadFile, margin );
    fdlTargetFile.right = new FormAttachment( middle, -margin );
    wlTargetFile.setLayoutData( fdlTargetFile );

    wbTargetFile = new Button( wTargetFileGroup, SWT.PUSH | SWT.CENTER );
    props.setLook( wbTargetFile );
    wbTargetFile.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    fdbTargetFile = new FormData();
    fdbTargetFile.right = new FormAttachment( 100, 0 );
    fdbTargetFile.top = new FormAttachment( wUploadFile, margin );
    wbTargetFile.setLayoutData( fdbTargetFile );

    wTargetFile = new TextVar( jobMeta, wTargetFileGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wTargetFile );
    wTargetFile.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.TargetFile.Tooltip" ) );
    wTargetFile.addModifyListener( lsMod );
    fdTargetFile = new FormData();
    fdTargetFile.left = new FormAttachment( middle, 0 );
    fdTargetFile.top = new FormAttachment( wUploadFile, margin );
    fdTargetFile.right = new FormAttachment( wbTargetFile, -margin );
    wTargetFile.setLayoutData( fdTargetFile );

    wbTargetFile.addSelectionListener( new SelectionAdapterFileDialogTextVar( jobMeta.getLogChannel(), wTargetFile, jobMeta,
      new SelectionAdapterOptions( jobMeta.getBowl(), SelectionOperation.SAVE_TO,
        new FilterType[] { FilterType.ALL }, FilterType.ALL  ) ) );

    // Append line
    wlAppend = new Label( wTargetFileGroup, SWT.RIGHT );
    wlAppend.setText( BaseMessages.getString( PKG, "JobHTTP.TargetFileAppend.Label" ) );
    props.setLook( wlAppend );
    fdlAppend = new FormData();
    fdlAppend.left = new FormAttachment( 0, 0 );
    fdlAppend.top = new FormAttachment( wTargetFile, margin );
    fdlAppend.right = new FormAttachment( middle, -margin );
    wlAppend.setLayoutData( fdlAppend );
    wAppend = new Button( wTargetFileGroup, SWT.CHECK );
    props.setLook( wAppend );
    wAppend.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.TargetFileAppend.Tooltip" ) );
    fdAppend = new FormData();
    fdAppend.left = new FormAttachment( middle, 0 );
    fdAppend.top = new FormAttachment( wTargetFile, margin );
    fdAppend.right = new FormAttachment( 100, 0 );
    wAppend.setLayoutData( fdAppend );

    // DateTimeAdded line
    wlDateTimeAdded = new Label( wTargetFileGroup, SWT.RIGHT );
    wlDateTimeAdded.setText( BaseMessages.getString( PKG, "JobHTTP.TargetFilenameAddDate.Label" ) );
    props.setLook( wlDateTimeAdded );
    fdlDateTimeAdded = new FormData();
    fdlDateTimeAdded.left = new FormAttachment( 0, 0 );
    fdlDateTimeAdded.top = new FormAttachment( wAppend, margin );
    fdlDateTimeAdded.right = new FormAttachment( middle, -margin );
    wlDateTimeAdded.setLayoutData( fdlDateTimeAdded );
    wDateTimeAdded = new Button( wTargetFileGroup, SWT.CHECK );
    props.setLook( wDateTimeAdded );
    wDateTimeAdded.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.TargetFilenameAddDate.Tooltip" ) );
    fdDateTimeAdded = new FormData();
    fdDateTimeAdded.left = new FormAttachment( middle, 0 );
    fdDateTimeAdded.top = new FormAttachment( wAppend, margin );
    fdDateTimeAdded.right = new FormAttachment( 100, 0 );
    wDateTimeAdded.setLayoutData( fdDateTimeAdded );
    wDateTimeAdded.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        setFlags();
      }
    } );

    // TargetExt line
    wlTargetExt = new Label( wTargetFileGroup, SWT.RIGHT );
    wlTargetExt.setText( BaseMessages.getString( PKG, "JobHTTP.TargetFileExt.Label" ) );
    props.setLook( wlTargetExt );
    fdlTargetExt = new FormData();
    fdlTargetExt.left = new FormAttachment( 0, 0 );
    fdlTargetExt.top = new FormAttachment( wDateTimeAdded, margin );
    fdlTargetExt.right = new FormAttachment( middle, -margin );
    wlTargetExt.setLayoutData( fdlTargetExt );
    wTargetExt = new TextVar( jobMeta, wTargetFileGroup, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wTargetExt );
    wTargetExt.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.TargetFileExt.Tooltip" ) );
    wTargetExt.addModifyListener( lsMod );
    fdTargetExt = new FormData();
    fdTargetExt.left = new FormAttachment( middle, 0 );
    fdTargetExt.top = new FormAttachment( wDateTimeAdded, margin );
    fdTargetExt.right = new FormAttachment( 100, 0 );
    wTargetExt.setLayoutData( fdTargetExt );

    // Add filenames to result filenames...
    wlAddFilenameToResult = new Label( wTargetFileGroup, SWT.RIGHT );
    wlAddFilenameToResult.setText( BaseMessages.getString( PKG, "JobHTTP.AddFilenameToResult.Label" ) );
    props.setLook( wlAddFilenameToResult );
    fdlAddFilenameToResult = new FormData();
    fdlAddFilenameToResult.left = new FormAttachment( 0, 0 );
    fdlAddFilenameToResult.top = new FormAttachment( wTargetExt, margin );
    fdlAddFilenameToResult.right = new FormAttachment( middle, -margin );
    wlAddFilenameToResult.setLayoutData( fdlAddFilenameToResult );
    wAddFilenameToResult = new Button( wTargetFileGroup, SWT.CHECK );
    wAddFilenameToResult.setToolTipText( BaseMessages.getString( PKG, "JobHTTP.AddFilenameToResult.Tooltip" ) );
    props.setLook( wAddFilenameToResult );
    fdAddFilenameToResult = new FormData();
    fdAddFilenameToResult.left = new FormAttachment( middle, 0 );
    fdAddFilenameToResult.top = new FormAttachment( wTargetExt, margin );
    fdAddFilenameToResult.right = new FormAttachment( 100, 0 );
    wAddFilenameToResult.setLayoutData( fdAddFilenameToResult );

    fdTargetFileGroup = new FormData();
    fdTargetFileGroup.left = new FormAttachment( 0, margin );
    fdTargetFileGroup.top = new FormAttachment( wUpLoadFile, margin );
    fdTargetFileGroup.right = new FormAttachment( 100, -margin );
    wTargetFileGroup.setLayoutData( fdTargetFileGroup );
    // ///////////////////////////////////////////////////////////
    // / END OF TargetFileGroupGROUP GROUP
    // ///////////////////////////////////////////////////////////

    fdGeneralComp = new FormData();
    fdGeneralComp.left = new FormAttachment( 0, 0 );
    fdGeneralComp.top = new FormAttachment( wName, 0 );
    fdGeneralComp.right = new FormAttachment( 100, 0 );
    fdGeneralComp.bottom = new FormAttachment( 100, 0 );
    wGeneralComp.setLayoutData( fdGeneralComp );

    wGeneralComp.layout();
    wGeneralTab.setControl( wGeneralComp );

    // ///////////////////////////////////////////////////////////
    // / END OF GENERAL TAB
    // ///////////////////////////////////////////////////////////

    // ////////////////////////
    // START OF Headers TAB ///
    // ////////////////////////

    wHeadersTab = new CTabItem( wTabFolder, SWT.NONE );
    wHeadersTab.setText( BaseMessages.getString( PKG, "JobHTTP.Tab.Headers.Label" ) );
    wHeadersComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wHeadersComp );
    FormLayout HeadersLayout = new FormLayout();
    HeadersLayout.marginWidth = 3;
    HeadersLayout.marginHeight = 3;
    wHeadersComp.setLayout( HeadersLayout );

    int rows =
      jobEntry.getHeaderName() == null ? 1 : ( jobEntry.getHeaderName().length == 0 ? 0 : jobEntry
        .getHeaderName().length );

    colinf =
      new ColumnInfo[] {

        new ColumnInfo(
          BaseMessages.getString( PKG, "JobHTTP.ColumnInfo.Name" ), ColumnInfo.COLUMN_TYPE_CCOMBO,
          HTTPProtocol.getRequestHeaders(), false ),

        new ColumnInfo(
          BaseMessages.getString( PKG, "JobHTTP.ColumnInfo.Value" ), ColumnInfo.COLUMN_TYPE_TEXT, false ), };
    colinf[0].setUsingVariables( true );
    colinf[1].setUsingVariables( true );

    wHeaders =
      new TableView(
        jobMeta, wHeadersComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, rows, lsMod, props );

    fdHeaders = new FormData();
    fdHeaders.left = new FormAttachment( 0, margin );
    fdHeaders.top = new FormAttachment( wName, margin );
    fdHeaders.right = new FormAttachment( 100, -margin );
    fdHeaders.bottom = new FormAttachment( 100, -margin );
    wHeaders.setLayoutData( fdHeaders );

    fdHeadersComp = new FormData();
    fdHeadersComp.left = new FormAttachment( 0, 0 );
    fdHeadersComp.top = new FormAttachment( 0, 0 );
    fdHeadersComp.right = new FormAttachment( 100, 0 );
    fdHeadersComp.bottom = new FormAttachment( 100, 0 );
    wHeadersComp.setLayoutData( fdHeadersComp );

    wHeadersComp.layout();
    wHeadersTab.setControl( wHeadersComp );

    // ///////////////////////////////////////////////////////////
    // / END OF Headers TAB
    // ///////////////////////////////////////////////////////////

    fdTabFolder = new FormData();
    fdTabFolder.left = new FormAttachment( 0, 0 );
    fdTabFolder.top = new FormAttachment( wName, margin );
    fdTabFolder.right = new FormAttachment( 100, 0 );
    fdTabFolder.bottom = new FormAttachment( 100, -50 );
    wTabFolder.setLayoutData( fdTabFolder );

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel }, margin, wTabFolder );

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

    wName.addSelectionListener( lsDef );
    wURL.addSelectionListener( lsDef );
    wTargetFile.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    getData();
    wTabFolder.setSelection( 0 );
    BaseStepDialog.setSize( shell );

    shell.open();
    props.setDialogSize( shell, "JobHTTPDialogSize" );
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return jobEntry;
  }

  private void setFlags() {
    wlURL.setEnabled( !wRunEveryRow.getSelection() );
    wURL.setEnabled( !wRunEveryRow.getSelection() );
    wlFieldURL.setEnabled( wRunEveryRow.getSelection() );
    wFieldURL.setEnabled( wRunEveryRow.getSelection() );
    wlFieldUpload.setEnabled( wRunEveryRow.getSelection() );
    wFieldUpload.setEnabled( wRunEveryRow.getSelection() );
    wlFieldTarget.setEnabled(  wRunEveryRow.getSelection() );
    wFieldTarget.setEnabled( wRunEveryRow.getSelection() );

    wlUploadFile.setEnabled( !wRunEveryRow.getSelection() );
    wUploadFile.setEnabled( !wRunEveryRow.getSelection() );
    wbUploadFile.setEnabled( !wRunEveryRow.getSelection() );
    wlTargetFile.setEnabled( !wRunEveryRow.getSelection() );
    wbTargetFile.setEnabled( !wRunEveryRow.getSelection() );
    wTargetFile.setEnabled( !wRunEveryRow.getSelection() );
    wlDateTimeAdded.setEnabled( !wRunEveryRow.getSelection() );
    wDateTimeAdded.setEnabled( !wRunEveryRow.getSelection() );
    wlAppend.setEnabled( wRunEveryRow.getSelection() ? false : !wDateTimeAdded.getSelection() );
    wAppend.setEnabled( wRunEveryRow.getSelection() ? false : !wDateTimeAdded.getSelection() );
    wlTargetExt.setEnabled( wRunEveryRow.getSelection() ? false : wDateTimeAdded.getSelection() );
    wTargetExt.setEnabled( wRunEveryRow.getSelection() ? false : wDateTimeAdded.getSelection() );
  }

  public void dispose() {
    WindowProperty winprop = new WindowProperty( shell );
    props.setScreen( winprop );
    shell.dispose();
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wName.setText( Const.NVL( jobEntry.getName(), "" ) );

    wURL.setText( Const.NVL( jobEntry.getUrl(), "" ) );
    wRunEveryRow.setSelection( jobEntry.isRunForEveryRow() );
    wFieldURL.setText( Const.NVL( jobEntry.getUrlFieldname(), "" ) );
    wFieldUpload.setText( Const.NVL( jobEntry.getUploadFieldname(), "" ) );
    wFieldTarget.setText( Const.NVL( jobEntry.getDestinationFieldname(), "" ) );
    wTargetFile.setText( Const.NVL( jobEntry.getTargetFilename(), "" ) );
    wAppend.setSelection( jobEntry.isFileAppended() );
    wDateTimeAdded.setSelection( jobEntry.isDateTimeAdded() );
    wTargetExt.setText( Const.NVL( jobEntry.getTargetFilenameExtension(), "" ) );

    wUploadFile.setText( Const.NVL( jobEntry.getUploadFilename(), "" ) );

    jobEntry.setDateTimeAdded( wDateTimeAdded.getSelection() );
    jobEntry.setTargetFilenameExtension( wTargetExt.getText() );

    wUserName.setText( Const.NVL( jobEntry.getUsername(), "" ) );
    wPassword.setText( Const.NVL( jobEntry.getPassword(), "" ) );

    wProxyServer.setText( Const.NVL( jobEntry.getProxyHostname(), "" ) );
    wProxyPort.setText( Const.NVL( jobEntry.getProxyPort(), "" ) );
    wNonProxyHosts.setText( Const.NVL( jobEntry.getNonProxyHosts(), "" ) );

    String[] headerNames = jobEntry.getHeaderName();
    String[] headerValues = jobEntry.getHeaderValue();
    if ( headerNames != null ) {
      for ( int i = 0; i < headerNames.length; i++ ) {
        TableItem ti = wHeaders.table.getItem( i );
        if ( headerNames[i] != null ) {
          ti.setText( 1, headerNames[i] );
        }
        if ( headerValues[i] != null ) {
          ti.setText( 2, headerValues[i] );
        }
      }
      wHeaders.setRowNums();
      wHeaders.optWidth( true );
    }

    wAddFilenameToResult.setSelection( jobEntry.isAddFilenameToResult() );
    setFlags();

    wName.selectAll();
    wName.setFocus();
  }

  private void cancel() {
    jobEntry.setChanged( changed );
    jobEntry = null;
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setText( BaseMessages.getString( PKG, "System.StepJobEntryNameMissing.Title" ) );
      mb.setMessage( BaseMessages.getString( PKG, "System.JobEntryNameMissing.Msg" ) );
      mb.open();
      return;
    }
    jobEntry.setName( wName.getText() );
    jobEntry.setUrl( wURL.getText() );
    jobEntry.setRunForEveryRow( wRunEveryRow.getSelection() );
    jobEntry.setUrlFieldname( wFieldURL.getText() );
    jobEntry.setUploadFieldname( wFieldUpload.getText() );
    jobEntry.setDestinationFieldname( wFieldTarget.getText() );

    jobEntry.setUsername( wUserName.getText() );
    jobEntry.setPassword( wPassword.getText() );
    jobEntry.setProxyHostname( wProxyServer.getText() );
    jobEntry.setProxyPort( wProxyPort.getText() );
    jobEntry.setNonProxyHosts( wNonProxyHosts.getText() );

    jobEntry.setUploadFilename( wUploadFile.getText() );

    jobEntry.setTargetFilename( wRunEveryRow.getSelection() ? "" : wTargetFile.getText() );
    jobEntry.setFileAppended( wRunEveryRow.getSelection() ? false : wAppend.getSelection() );
    jobEntry.setDateTimeAdded( wRunEveryRow.getSelection() ? false : wDateTimeAdded.getSelection() );
    jobEntry.setTargetFilenameExtension( wRunEveryRow.getSelection() ? "" : wTargetExt.getText() );
    jobEntry.setAddFilenameToResult( wAddFilenameToResult.getSelection() );

    int nritems = wHeaders.nrNonEmpty();
    int nr = 0;
    for ( int i = 0; i < nritems; i++ ) {
      String arg = wHeaders.getNonEmpty( i ).getText( 1 );
      if ( arg != null && arg.length() != 0 ) {
        nr++;
      }
    }
    String[] headerNames = new String[nr];
    String[] headerValues = new String[nr];

    nr = 0;
    for ( int i = 0; i < nritems; i++ ) {
      String varname = wHeaders.getNonEmpty( i ).getText( 1 );
      String varvalue = wHeaders.getNonEmpty( i ).getText( 2 );

      if ( varname != null && varname.length() != 0 ) {
        headerNames[nr] = varname;
        headerValues[nr] = varvalue;
        nr++;
      }
    }
    jobEntry.setHeaderName( headerNames );
    jobEntry.setHeaderValue( headerValues );

    dispose();
  }

  public boolean evaluates() {
    return true;
  }

  public boolean isUnconditional() {
    return false;
  }

}
