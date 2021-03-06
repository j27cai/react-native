/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.react.uimanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.DisplayMetrics;

import com.facebook.react.common.MapBuilder;

/**
 * Helps generate constants map for {@link UIManagerModule} by collecting and merging constants from
 * registered view managers.
 */
/* package */ class UIManagerModuleConstantsHelper {

  private static final String CUSTOM_BUBBLING_EVENT_TYPES_KEY = "customBubblingEventTypes";
  private static final String CUSTOM_DIRECT_EVENT_TYPES_KEY = "customDirectEventTypes";

  /**
   * Generates map of constants that is then exposed by {@link UIManagerModule}. The constants map
   * contains the following predefined fields for 'customBubblingEventTypes' and
   * 'customDirectEventTypes'. Provided list of {@param viewManagers} is then used to populate
   * content of those predefined fields using
   * {@link ViewManager#getExportedCustomBubblingEventTypeConstants} and
   * {@link ViewManager#getExportedCustomDirectEventTypeConstants} respectively. Each view manager
   * is in addition allowed to expose viewmanager-specific constants that are placed under the key
   * that corresponds to the view manager's name (see {@link ViewManager#getName}). Constants are
   * merged into the map of {@link UIManagerModule} base constants that is stored in
   * {@link UIManagerModuleConstants}.
   * TODO(6845124): Create a test for this
   */
  /* package */ static Map<String, Object> createConstants(
      DisplayMetrics displayMetrics,
      List<ViewManager> viewManagers) {
    Map<String, Object> constants = UIManagerModuleConstants.getConstants(displayMetrics);
    Map bubblingEventTypesConstants = UIManagerModuleConstants.getBubblingEventTypeConstants();
    Map directEventTypesConstants = UIManagerModuleConstants.getDirectEventTypeConstants();

    for (ViewManager viewManager : viewManagers) {
      Map viewManagerBubblingEvents = viewManager.getExportedCustomBubblingEventTypeConstants();
      if (viewManagerBubblingEvents != null) {
        recursiveMerge(bubblingEventTypesConstants, viewManagerBubblingEvents);
      }
      Map viewManagerDirectEvents = viewManager.getExportedCustomDirectEventTypeConstants();
      if (viewManagerDirectEvents != null) {
        recursiveMerge(directEventTypesConstants, viewManagerDirectEvents);
      }
      Map viewManagerConstants = MapBuilder.newHashMap();
      Map customViewConstants = viewManager.getExportedViewConstants();
      if (customViewConstants != null) {
        viewManagerConstants.put("Constants", customViewConstants);
      }
      Map viewManagerCommands = viewManager.getCommandsMap();
      if (viewManagerCommands != null) {
        viewManagerConstants.put("Commands", viewManagerCommands);
      }
      Map<String, UIProp.Type> viewManagerNativeProps = viewManager.getNativeProps();
      if (!viewManagerNativeProps.isEmpty()) {
        Map<String, String> nativeProps = new HashMap<>();
        for (Map.Entry<String, UIProp.Type> entry : viewManagerNativeProps.entrySet()) {
          nativeProps.put(entry.getKey(), entry.getValue().toString());
        }
        viewManagerConstants.put("NativeProps", nativeProps);
      }
      if (!viewManagerConstants.isEmpty()) {
        constants.put(viewManager.getName(), viewManagerConstants);
      }
    }

    constants.put(CUSTOM_BUBBLING_EVENT_TYPES_KEY, bubblingEventTypesConstants);
    constants.put(CUSTOM_DIRECT_EVENT_TYPES_KEY, directEventTypesConstants);

    return constants;
  }

  /**
   * Merges {@param source} map into {@param dest} map recursively
   */
  private static void recursiveMerge(Map dest, Map source) {
    for (Object key : source.keySet()) {
      Object sourceValue = source.get(key);
      Object destValue = dest.get(key);
      if (destValue != null && (sourceValue instanceof Map) && (destValue instanceof Map)) {
        recursiveMerge((Map) destValue, (Map) sourceValue);
      } else {
        dest.put(key, sourceValue);
      }
    }
  }
}
