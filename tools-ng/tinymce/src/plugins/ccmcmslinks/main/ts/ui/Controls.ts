/**
 * Copyright (c) Tiny Technologies, Inc. All rights reserved.
 * Licensed under the LGPL or a commercial license.
 * For LGPL see License.txt in the project root for license information.
 * For commercial licenses see https://www.tiny.cloud/
 */

import Actions from '../core/Actions';
import Utils from '../core/Utils';

const setupButtons = function (editor) {
  editor.addButton('ccm-cms-link', {
    active: false,
    icon: 'link',
    tooltip: 'Insert/edit link',
    onclick: Actions.openDialog(editor),
    onpostrender: Actions.toggleActiveState(editor)
  });

  editor.addButton('ccm-cms-unlink', {
    active: false,
    icon: 'unlink',
    tooltip: 'Remove link',
    onclick: Utils.unlink(editor),
    onpostrender: Actions.toggleActiveState(editor)
  });

  if (editor.addContextToolbar) {
    editor.addButton('ccm-cms-openlink', {
      icon: 'newtab',
      tooltip: 'Open link',
      onclick: Actions.gotoSelectedLink(editor)
    });
  }
};

const setupMenuItems = function (editor) {
  editor.addMenuItem('ccm-cms-openlink', {
    text: 'Open link',
    icon: 'newtab',
    onclick: Actions.gotoSelectedLink(editor),
    onPostRender: Actions.toggleViewLinkState(editor),
    prependToContext: true
  });

  editor.addMenuItem('ccm-cms-link', {
    icon: 'link',
    text: 'Link',
    shortcut: 'Meta+K',
    onclick: Actions.openDialog(editor),
    stateSelector: 'a[href]',
    context: 'insert',
    prependToContext: true
  });

  editor.addMenuItem('ccm-cms-unlink', {
    icon: 'unlink',
    text: 'Remove link',
    onclick: Utils.unlink(editor),
    stateSelector: 'a[href]'
  });
};

const setupContextToolbars = function (editor) {
  if (editor.addContextToolbar) {
    editor.addContextToolbar(
      Actions.leftClickedOnAHref(editor),
      'ccm-cms-openlink | ccm-cms-link ccm-cms-unlink'
    );
  }
};

export default {
  setupButtons,
  setupMenuItems,
  setupContextToolbars
};
