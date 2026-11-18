import javax.swing.*;
import java.awt.*;

// MainFrame acts as the SMS Scholarship Committee Dashboard (self-contained, no Theme.java required)
public class MainFrame extends JFrame {

    private static final Color NAVY = new Color(16, 42, 67);
    private static final Color SIDEBAR_TEXT = new Color(214, 224, 235);
    private static final Color SIDEBAR_MUTED = new Color(176, 196, 214);
    private static final Color BG_LIGHT = new Color(244, 247, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_GRAY = new Color(220, 227, 236);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);

    private static final Font BRAND_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font SECTION_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 13);
    private static final Font SIDEBAR_USER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 13);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);

    public MainFrame() {

        setTitle("SMS - Committee Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Left Navy Sidebar
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setOpaque(false);

        JLabel brand = new JLabel("SMS");
        brand.setFont(BRAND_FONT);
        brand.setForeground(Color.WHITE);
        sidebarTop.add(brand);
        sidebarTop.add(Box.createVerticalStrut(22));

        JLabel name = new JLabel("Scholarship Committee");
        name.setFont(SIDEBAR_USER_FONT);
        name.setForeground(Color.WHITE);
        sidebarTop.add(name);

        JLabel role = new JLabel("Committee Portal");
        role.setFont(SMALL_FONT);
        role.setForeground(SIDEBAR_MUTED);
        sidebarTop.add(role);
        sidebarTop.add(Box.createVerticalStrut(14));

        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(40, 72, 104));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebarTop.add(divider);
        sidebarTop.add(Box.createVerticalStrut(16));

        sidebarTop.add(createSidebarBtn("Manage Scholarship", () ->
            JOptionPane.showMessageDialog(this, "Manage Scholarship (UC-2)")));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarBtn("Advertisement", () ->
            JOptionPane.showMessageDialog(this, "Advertisement (UC-3)")));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarBtn("Verifying Applications", () -> {
            new VerifyApplicationFrame();
            dispose();
        }));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarBtn("Schedule Interview", () ->
            JOptionPane.showMessageDialog(this, "Schedule Interview (UC-7)")));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarBtn("Conduct Interview", () ->
            JOptionPane.showMessageDialog(this, "Conduct Interview (UC-9)")));
        sidebarTop.add(Box.createVerticalStrut(6));
        sidebarTop.add(createSidebarBtn("Feedback", () ->
            JOptionPane.showMessageDialog(this, "Feedback (UC-11)")));

        sidebar.add(sidebarTop, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);

        // Right Workspace
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_LIGHT);
        main.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel welcome = new JLabel("Welcome, Committee Member");
        welcome.setFont(HEADER_FONT);
        welcome.setForeground(TEXT_DARK);
        header.add(welcome);

        JLabel subtitle = new JLabel("Committee Dashboard | Pick an option from the menu or cards below.");
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(TEXT_MUTED);
        header.add(subtitle);
        main.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 2, 16, 16));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(24, 0, 20, 0));

        grid.add(createCard("Manage Scholarship", "Create and update Need based or Merit based scholarships.", () ->
            JOptionPane.showMessageDialog(this, "Manage Scholarship (UC-2)")
        ));
        grid.add(createCard("Advertisement", "Advertise scholarships so students can view and apply.", () ->
            JOptionPane.showMessageDialog(this, "Advertisement (UC-3)")
        ));
        grid.add(createCard("Verifying Applications", "Review submitted applications, check documents, and verify eligibility.", () -> {
            new VerifyApplicationFrame();
            dispose();
        }));
        grid.add(createCard("Schedule Interview", "Add available date and time slots for student interviews.", () ->
            JOptionPane.showMessageDialog(this, "Schedule Interview (UC-7)")
        ));
        grid.add(createCard("Conduct Interview", "Launch scheduled student interviews and record assessment scores.", () ->
            JOptionPane.showMessageDialog(this, "Conduct Interview (UC-9)")
        ));
        grid.add(createCard("Feedback", "Generate feedback remarks for each participant's application.", () ->
            JOptionPane.showMessageDialog(this, "Feedback (UC-11)")
        ));

        main.add(grid, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }

    private JButton createSidebarBtn(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(SIDEBAR_FONT);
        btn.setForeground(SIDEBAR_TEXT);
        btn.setBackground(NAVY);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JPanel createCard(String title, String desc, Runnable onOpen) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel t = new JLabel(title);
        t.setFont(SECTION_FONT);
        t.setForeground(TEXT_DARK);
        text.add(t);
        text.add(Box.createVerticalStrut(6));

        JLabel d = new JLabel("<html><body style='width:240px'>" + desc + "</body></html>");
        d.setFont(SMALL_FONT);
        d.setForeground(TEXT_MUTED);
        text.add(d);

        JButton openBtn = new JButton("Open ->");
        openBtn.setFont(BUTTON_FONT);
        openBtn.setForeground(NAVY);
        openBtn.setContentAreaFilled(false);
        openBtn.setBorderPainted(false);
        openBtn.setHorizontalAlignment(SwingConstants.LEFT);
        openBtn.addActionListener(e -> onOpen.run());

        card.add(text, BorderLayout.CENTER);
        card.add(openBtn, BorderLayout.SOUTH);
        return card;
    }
}