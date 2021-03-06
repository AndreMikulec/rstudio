/*
 * AnchorableFrame.java
 *
 * Copyright (C) 2009-12 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

package org.rstudio.core.client.widget;

import org.rstudio.core.client.dom.IFrameElementEx;
import org.rstudio.core.client.dom.WindowEx;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.ui.Frame;

public class AnchorableFrame extends Frame
{
   public AnchorableFrame()
   {
      this(true);
   }
   
   public AnchorableFrame(boolean autoFocus)
   {
      autoFocus_ = autoFocus;
      setStylePrimaryName("rstudio-HelpFrame");
      getElement().getStyle().setBackgroundColor("white");
   }
       
   public void navigate(final String url)
   {
      RepeatingCommand navigateCommand = new RepeatingCommand() {
         @Override
         public boolean execute()
         {
            if (getIFrame() != null && getWindow() != null)
            {
               // if this is the same page but without an anchor qualification
               // then reload (so we preserve the anchor location)
               if (isBasePageOfCurrentAnchor(url))
               {
                  getWindow().reload();
               }
               // if it's the same page then set the anchor and force a reload
               else if (isSamePage(url))
               {
                  getWindow().replaceLocationHref(url);
                  getWindow().reload();
               }
               // otherwise a new url, merely replacing will force a reload
               else
               {
                  getWindow().replaceLocationHref(url);
               }
               
               if (autoFocus_)
                  getWindow().focus();
               
               return false;
            }
            else
            {
               return true;
            }
         }
      };

      if (navigateCommand.execute())
         Scheduler.get().scheduleFixedDelay(navigateCommand, 50);      
   }
   
   public WindowEx getWindow()
   {
      return getIFrame().getContentWindow();
   }
   

   
   private boolean isBasePageOfCurrentAnchor(String newUrl)
   {
      // make sure there is an existing url to compare to
      String existingUrl = getWindow().getLocationHref();
      if (existingUrl == null)
         return false;      
     
      return newUrl.equals(stripAnchor(existingUrl));
   }
   
   private boolean isSamePage(String newUrl)
   {
      // make sure there is an existing url to compare to
      String existingUrl = getWindow().getLocationHref();
      if (existingUrl == null)
         return false;      
      
      return stripAnchor(newUrl).equals(stripAnchor(existingUrl));
   }
   
   private IFrameElementEx getIFrame()
   {
      return getElement().cast();
   }
   
   private String stripAnchor(String url)
   {
      int hashPos = url.lastIndexOf('#');
      if (hashPos != -1)
         return url.substring(0, hashPos);
      else
         return url;
   }
   
   private final boolean autoFocus_;

}