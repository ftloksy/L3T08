import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class TabChangeListener implements ChangeListener {
    TabFrame mainFrame;
    //MenuHandler dbMenu ;
    
    TabChangeListener(TabFrame motherFrame) {
        super();
        this.mainFrame = motherFrame;
        //this.dbMenu = this.mainFrame.PosieDatabaseMenu ;
    }

    public void stateChanged(ChangeEvent changeEvent) {
        JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
        int index = sourceTabbedPane.getSelectedIndex();
        switch ( index ) {
            case 0:
                this.mainFrame.PosieDatabaseMenu.clearItem.setText("Clear Person");
                this.mainFrame.PosieDatabaseMenu.enterItem.setText("Enter Person");
                this.mainFrame.PosieDatabaseMenu.updateItem.setText("Update Person");
                this.mainFrame.PosieDatabaseMenu.deleteItem.setText("Delete Person");
                this.mainFrame.PosieDatabaseMenu.searchItem.setText("Search Person");
                this.mainFrame.PosieDatabaseMenu.listAllItem.setText("ListAll Person");
                break;
            case 1:
                this.mainFrame.PosieDatabaseMenu.clearItem.setText("Clear Poised");
                this.mainFrame.PosieDatabaseMenu.enterItem.setText("Enter Poised");
                this.mainFrame.PosieDatabaseMenu.updateItem.setText("Update Poised");
                this.mainFrame.PosieDatabaseMenu.deleteItem.setText("Delete Poised");
                this.mainFrame.PosieDatabaseMenu.searchItem.setText("Search Poised");
                this.mainFrame.PosieDatabaseMenu.listAllItem.setText("ListAll Poised");
                break;
        }
        //System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
        //System.out.println("Tab changed to [INDEX]: " + index);
    }
}
