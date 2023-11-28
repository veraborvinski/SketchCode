/**
 * you can put a one sentence description of your tool here.
 *
 * (c) 2015
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author   Vera Borvinski https://github.com/veraborvinski
 * @modified 11/23/2023
 * @version  1.0.0
 */

package template.tool;

//import processing.app.Base;

//import processing.app.tools.Tool;
//import processing.app.ui.Editor;

import processing.app.*;
import processing.app.tools.*;

// when creating a tool, the name of the main class which implements Tool must
// be the same as the value defined for project.name in your build.properties

public class SketchToCode implements Tool {
  Base base;


  public String getMenuTitle() {
    return "SketchToCode";
  }


  public void init(Base base) {
    // Store a reference to the Processing application itself
    this.base = base;
  }

  @Override
  public void run() {

    // Fill in author.name, author.url, tool.prettyVersion and
    // project.prettyName in build.properties for them to be auto-replaced here.
    System.out.println("Hello Tool. SketchToCode 1.0.0 by Vera Borvinski https://github.com/veraborvinski");
	
    GUIFrame gui = new GUIFrame();
    gui.showGUI(base);
  }

  public static void main(String[] args) {
		new SketchToCode().run();
  }
}
