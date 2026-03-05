package restoran;

import restoran.ui.LoginForm;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Postavi Nimbus Look and Feel (moderan izgled)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Koristiti default L&F ako Nimbus nije dostupan
        }

        SwingUtilities.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setVisible(true);
        });
    }
}
