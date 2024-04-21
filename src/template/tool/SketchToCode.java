/**
 * The SketchCode tool is used to generate a Processing sketch from a GUI.
 * 
 * Author: Vera Borvinski
 * Matriculation number: 2421818
 * 
 * This tool uses the Processing tool template from https://github.com/processing/processing-tool-template
 *
 * ##copyright##
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
 * @author   Vera Borvinski
 * @modified 20/4-24
 * @version  1.0
 */

package template.tool;

import processing.app.*;
import processing.app.tools.*;

/** 
* The SketchToCode class is used to start the SketchCode tool
* 
* @author Vera Borvinski
*/
public class SketchToCode implements Tool {
  Base base;

  /** 
   * This method is used to get the name of the tool as displayed in the menu. 
   * @return String This returns the name of the tool. 
   */
  public String getMenuTitle() {
    return "##tool.name##";
  }

  /** 
   * This method is used to initialise the Processing editor. 
   * @param base This is the processing application. 
   * @return void Nothing. 
   */
  public void init(Base base) {
    // Store a reference to the Processing application itself
    this.base = base;
  }

  /** 
   * Starts the tool. 
   * @return void Nothing. 
   */
  @Override
  public void run() {

    // Fill in author.name, author.url, tool.prettyVersion and
    // project.prettyName in build.properties for them to be auto-replaced here.
    System.out.println("Hello Tool. ##tool.name## ##tool.prettyVersion## by ##author##");
	
    GUIFrame gui = new GUIFrame();
    gui.showGUI(base);
  }

  /** 
   * Main ethod, calls the run function for the tool. 
   * @param args Nothing. 
   * @return void Nothing. 
   */
  public static void main(String[] args) {
		new SketchToCode().run();
  }
}
