package gui;

import java.util.ArrayList;
import java.awt.Color;

/**
 *
 * @author mandim
 */
public class GuiUpdater {

    public static Gui window = null;

    
    public GuiUpdater(Gui window) {
        GuiUpdater.window = window;
    }

    public static void updateLog(String logMessage, Color textColor) {
        window.log.setForeground(textColor);
        window.log.append(logMessage + "\n");
    }

    public static void updateLog(String logMessage) {
        window.log.setForeground(Color.BLACK);
        window.log.append(logMessage + "\n");
    }

    public static void updateCombobox(ArrayList<String> list) {

        for (int i = 0; i < list.size(); i++) {
            window.portList.addItem(list.get(i));
        }
    }
}
