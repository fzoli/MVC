package org.dyndns.fzoli.mvc.test.client.frame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.dyndns.fzoli.http.HttpUrl;
import org.dyndns.fzoli.http.desktop.DefaultHttpExecutor;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.connection.JSONConnection;
import org.dyndns.fzoli.mvc.client.event.CachedModelChangeListener;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.client.event.ModelChangeEvent;
import org.dyndns.fzoli.mvc.test.client.model.TestModel;
import org.dyndns.fzoli.mvc.test.common.pojo.TestData;
import org.dyndns.fzoli.mvc.test.common.pojo.TestEvent;

/**
 *
 * @author zoli
 */
public class TestFrame extends JFrame implements CachedModelChangeListener<TestEvent> {
    
    private final static Connection<Object, Object> conn = new JSONConnection(new HttpUrl(false, "localhost", 8084), new DefaultHttpExecutor(),"MVC", "Controller", "ChangeListener");
    private final TestModel testModel;
    
    public TestFrame() {
        // <editor-fold defaultstate="collapsed" desc="Frame init">
        super("Teszt");
        tf.setDocument(new JTextFieldLimit(limit));
        tf.setHorizontalAlignment(SwingConstants.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        lb.setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 5, 5);
        add(lb, c);
        c.insets = new Insets(0, 5, 5, 5);
        c.gridy = 2;
        c.gridwidth = 3;
        c.fill = c.HORIZONTAL;
        add(tf, c);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(this);
        //</editor-fold>
        //conn.getHttpExecutor().wrapHttpClient(); //HTTPS kapcsolat és önaláírt tanusítvány esetén szükséges
        updateStr("Töltés...", false);
        testModel = new TestModel(conn, new ModelActionListener<TestData>() {

            @Override
            public void modelActionPerformed(ModelActionEvent<TestData> e) {
                switch (e.getType()) {
                    case ModelActionEvent.TYPE_EVENT:
                        updateStr(e.getEvent().getStr(), true);
                        ((TestModel)e.getSourceModel()).addListener(TestFrame.this);
                        tf.addKeyListener(new KeyAdapter() {

                            @Override
                            public void keyReleased(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                    final String txt = tf.getText();
                                    if (txt.equals("Éva")) {
                                        showWarn(txt, "nagyon rossz");
                                    }
                                    else {
                                        tf.setEditable(false);
                                        testModel.setStr(txt, new ModelActionListener<Integer>() {

                                            @Override
                                            public void modelActionPerformed(ModelActionEvent<Integer> e) {
                                                int ret = -1;
                                                tf.setEditable(true);
                                                switch (e.getType()) {
                                                    case ModelActionEvent.TYPE_EVENT:
                                                        ret = e.getEvent();
                                                        break;
                                                    case ModelActionEvent.TYPE_CONNECTION_EXCEPTION:
                                                        updateStr(e.getConnectionException().getMessage(), false);
                                                        break;
                                                    case ModelActionEvent.TYPE_CONTROLLER_CLOSE_EXCEPTION:
                                                        updateStr(e.getControllerCloseException().getReason(), false);
                                                }
                                                if (ret != 0 && ret != -1) {
                                                    showWarn(txt, "rossz");
                                                }
                                            }

                                        });
                                    }
                                }
                            }

                            });
                        break;
                    case ModelActionEvent.TYPE_CONNECTION_EXCEPTION:
                        updateStr(e.getConnectionException().getMessage(), false);
                        break;
                    case ModelActionEvent.TYPE_CONTROLLER_CLOSE_EXCEPTION:
                        updateStr(e.getControllerCloseException().getReason(), false);
                }
            }
            
        });
        setVisible(true);
    }
    
    private void updateStr(String str, boolean visible) {
        tf.setVisible(visible);
        if (str != null) {
            lb.setText((visible ? "Helló" : "") + (str.length() == 0 ? str : " " + str) + (visible ? "!" : ""));
            if (visible) {
                tf.setText(str.substring(0, str.length() >= limit ? limit : str.length()));
                tf.setCaretPosition(tf.getText().length());
            }
        }
    }

    @Override
    public void fireCacheReload(int type) {
        updateStr("Újratöltés...", false);
    }
    
    @Override
    public void fireModelChanged(ModelChangeEvent<TestEvent> e) {
        if (e.getSourceModel() == testModel) {
            switch (e.getType()) {
                case ModelChangeEvent.TYPE_EVENT:
                    updateStr(e.getEvent().getStr(), true);
                    break;
                case ModelChangeEvent.TYPE_SERVER_LOST:
                    updateStr("A szerver eltűnt", false);
                    break;
                case ModelChangeEvent.TYPE_SERVER_RECONNECT:
                    updateStr(testModel.getStr(), true); //cached model esetén nem kell restart esetén kivételt ellenőrizni getStr-re
                    break;
                case ModelChangeEvent.TYPE_CONNECTION_EXCEPTION:
                    updateStr(e.getConnectionException().getMessage(), false);
            }
        }
    }
    
    private void showWarn(String txt, String s) {
        updateStr(txt + " " + s + "!", false);
        Timer t = new Timer(1500, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try { //nem árt ellenőrizni, de mivel elrejtődik az input mező hiba esetén, nem lesz kivétel
                    updateStr(testModel.getStr(), true);
                }
                catch(Exception ex) {
                    updateStr("Ez a hiba a tesztben nem lehetséges.", false);
                }
            }

        });
        t.setRepeats(false);
        t.start();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Init">
    private int limit = 12;
    private JLabel lb = new JLabel("...");
    private JTextField tf = new JTextField(10);
    
    private class JTextFieldLimit extends PlainDocument {
        
        private int limit;
        
        public JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limit) super.insertString(offset, str, attr);
        }
        
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}
        new TestFrame();
    }
    // </editor-fold>
    
}