/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package modmanager.gui.views;

import modmanager.business.Mod;
import modmanager.gui.ManagerCtrl;
import modmanager.gui.ManagerGUI;
import modmanager.gui.ModsTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * MouseAdapter for ModsTableViews to show the right-click menu, de/select mods
 * on double click, and potentially more.
 */
public class TableViewMouseListener extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) {
        Mod mod;

        try {
            mod = ManagerGUI.getInstance().getModsTable().getModAtPoint(e.getPoint());
        } catch(IndexOutOfBoundsException ex) {
            // Expected exception here if the mouse was not released over a
            // mod on the table.
            return;
        }
        
        if (e.isPopupTrigger()) { // Show context menu
            ManagerGUI.getInstance().preparePopupMenu(mod);

            // This is actually required, not just a design decision.  When
            // options are selected, they use the same handlers as the "normal"
            // buttons, and the actions apply themselves to the selected mod.
            ManagerGUI.getInstance().getModsTable().setSelectedMod(mod);

            ManagerGUI.getInstance().getRightClickTableMenu().show(e.getComponent(), e.getX(), e.getY());
        } else if(e.getClickCount() == 2) { // Double click - toggle current mod
            ManagerCtrl.getInstance().enableMod(mod);
            ManagerGUI.getInstance().displayModDetail(mod);
            ManagerGUI.getInstance().getModsTable().redraw();
        }
    }
}
