import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {
    private static JTextArea textArea1;
    private static JTextArea textArea2;

    GUI(){
        super("Mahmoud's blockchain explorer!!");
        JTabbedPane tabbedPane = new JTabbedPane();
        textArea1 = new JTextArea(39 , 95);
        textArea1.setBackground(Color.ORANGE);
        JPanel panel1 = new JPanel();
        panel1.add(textArea1);
        panel1.add(new JScrollPane(textArea1));
        panel1.setBackground(Color.ORANGE);
        tabbedPane.addTab("The BlockChain" , null , panel1 , "All of the chain's blocks");

        textArea2 = new JTextArea(39 , 95);
        textArea2.setBackground(Color.GREEN);
        JPanel panel2 = new JPanel();
        panel2.add(textArea2);
        panel2.add(new JScrollPane(textArea2));
        panel2.setBackground(Color.GREEN);
        tabbedPane.addTab("The Users" , null , panel2 , "What the users are doing");

        add(tabbedPane);
    }

    static synchronized void logTo(int tab , String content){
        if(tab == 1)
            textArea1.append(content);
        if(tab == 2)
            textArea2.append(content);
    }
}
