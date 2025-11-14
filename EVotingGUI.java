import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

class Voter {
    String id;
    String name;
    String password;
    boolean hasVoted;
    Voter next;

    Voter(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.hasVoted = false;
        this.next = null;
    }
}
class Candidate {
    String id;
    String name;
    String party;
    int votes;

    Candidate(String id, String name, String party) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.votes = 0;
    }
}

/* A custom singly linked list*/
class VoterList {
    private Voter head;
    /* ALGORITHM: Traversal */
    public void add(Voter newVoter) {
        if (head == null) {
            head = newVoter;
        } else {
            Voter current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newVoter;
        }
    }
    /* ALGORITHM: Linear Search*/
    public Voter findById(String id) {
        Voter current = head;
        while (current != null) {
            if (current.id.equals(id)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }
    public int count() {
        int c = 0;
        Voter t = head;
        while (t != null) {
            c++;
            t = t.next;
        }
        return c;
    }
}
/* A custom dynamic array*/
class CandidateList {
    private Candidate[] candidates;
    private int size;
    private int capacity;

    public CandidateList() {
        capacity = 10;
        candidates = new Candidate[capacity];
        size = 0;
    }
    public void add(Candidate newCandidate) {
        if (size == capacity) {
            capacity *= 2;
            Candidate[] newArray = new Candidate[capacity];
            for (int i = 0; i < size; i++) {
                newArray[i] = candidates[i];
            }
            candidates = newArray;
        }
        candidates[size] = newCandidate;
        size++;
    }
    public Candidate findById(String id) {
        for (int i = 0; i < size; i++) {
            if (candidates[i] != null && candidates[i].id.equals(id)) {
                return candidates[i];
            }
        }
        return null;
    }

    public int size() {
        return size;
    }

    public Candidate[] getRawArray() {
        return Arrays.copyOf(candidates, size);
    }

    /** ALGORITHM: Bubble Sort
     * Sorts candidates in descending order based on votes. */
    public void sortByVotes() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                 if (candidates[j] != null && candidates[j + 1] != null && candidates[j].votes < candidates[j + 1].votes) {
                    Candidate temp = candidates[j];
                    candidates[j] = candidates[j + 1];
                    candidates[j + 1] = temp;
                }
            }
        }
    }
    
    public String resultsText() {
        sortByVotes();
        String result = "--- Election Results ---\n";
        for (int i = 0; i < size; i++) {
            if(candidates[i] != null) result += String.format("%s (%s): %d votes\n", candidates[i].name, candidates[i].party, candidates[i].votes);
        }
        result += "\nTotal Votes Cast: " + totalVotes();
        return result;
    }
    
    public int totalVotes() {
        int total = 0;
        for (int i = 0; i < size; i++) {
            if(candidates[i] != null) total += candidates[i].votes;
        }
        return total;
    }
}

/* A stack-based log */
class AuditLog {
    private Stack<String> stack = new Stack<>();

    public void push(String s) {
        stack.push(new Date() + ": " + s);
    }

    public String allLogs() {
        if (stack.isEmpty()) return "No logs yet.";
        String logs = "";
        for (String log: stack) {
            logs += log + "\n";
        }
        return logs;
    }
}

class ElectionController {
    VoterList voters = new VoterList();
    CandidateList candidates = new CandidateList();
    AuditLog logs = new AuditLog();
    final String adminId = "admin";
    final String adminPass = "admin123";

    public ElectionController() {
        voters.add(new Voter("voter001", "Alice Smith", ""));
        voters.add(new Voter("voter002", "Bob Johnson", ""));
        voters.add(new Voter("voter003", "Charlie Ray", ""));
        voters.add(new Voter("voter004", "Diana Ray", ""));
        candidates.add(new Candidate("C1", "Eleanor Vance", "Future Forward"));
        candidates.add(new Candidate("C2", "Marcus Thorne", "Pioneer Alliance"));
        candidates.add(new Candidate("C3", "Isabella Rossi", "Liberty Union"));
        candidates.add(new Candidate("C4", "Julian Sato", "Innovate & Progress"));
    }

    public boolean adminLogin(String id, String pass) {
        boolean ok = adminId.equals(id) && adminPass.equals(pass);
        logs.push("Admin login attempt for id: " + id + " -> " + (ok ? "SUCCESS" : "FAILED"));
        return ok;
    }

    public boolean voterLogin(String id, String pass) {
        Voter v = voters.findById(id);
        boolean ok = (v != null) && v.password.equals(pass);
        logs.push("Voter login attempt for id: " + id + " -> " + (ok ? "SUCCESS" : "FAILED"));
        return ok;
    }

    public void addVoter(String id, String name, String pass) {
        voters.add(new Voter(id, name, pass));
        logs.push("Admin added voter: " + id + " - " + name);
    }

    public void addCandidate(String id, String name, String party) {
        candidates.add(new Candidate(id, name, party));
        logs.push("Admin added candidate: " + name + " (" + party + ")");
    }

    public String castVote(String voterId, String candidateId) {
        Voter v = voters.findById(voterId);
        if (v == null) {
            logs.push("Vote attempt FAILED - voter not found: " + voterId);
            return "Voter not found.";
        }
        if (v.hasVoted) {
            logs.push("Vote attempt FAILED - duplicate vote by: " + voterId);
            return "You have already voted.";
        }
        Candidate c = candidates.findById(candidateId);
        if (c == null) {
            logs.push("Vote attempt FAILED - invalid candidate ID by: " + voterId);
            return "Invalid candidate selected.";
        }
        c.votes++;
        v.hasVoted = true;
        logs.push("Vote cast SUCCESS by voter: " + voterId);
        return "SUCCESS";
    }
}
// Main GUI Class
public class EVotingGUI extends JFrame {
    private ElectionController controller = new ElectionController();
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards;
    private AdminPanel adminPanel;
    private VoterPortalPanel voterPortalPanel;

    public EVotingGUI() {
        setTitle("Online Voting System");
        setSize(960, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        cards = new JPanel(cardLayout);
        adminPanel = new AdminPanel();
        voterPortalPanel = new VoterPortalPanel();
        cards.add(new LoginPanel(), "login");
        cards.add(adminPanel, "admin");
        cards.add(voterPortalPanel, "voterPortal");
        add(cards);
    }
    
    private void showPanel(String name) {
        cardLayout.show(cards, name);
    }
    class LoginPanel extends JPanel {
        JTextField idField;
        JPasswordField passField;

        public LoginPanel() {
            setLayout(new GridBagLayout());
            setBackground(new Color(245, 247, 250));
            JPanel card = new JPanel();
            card.setPreferredSize(new Dimension(420, 420));
            card.setBackground(Color.white);
            card.setBorder(new CompoundBorder(new EmptyBorder(18, 18, 18, 18), new LineBorder(new Color(230, 230, 230))));
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            JLabel title = new JLabel("Online Voting System");
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
            JPanel form = new JPanel(new GridLayout(3, 1, 6, 6));
            form.setOpaque(false);
            idField = new JTextField(20);
            idField.setBorder(BorderFactory.createTitledBorder("Voter ID / Admin ID"));
            passField = new JPasswordField(20);
            passField.setBorder(BorderFactory.createTitledBorder("Password"));
            JButton loginBtn = new JButton("Login");
            loginBtn.setBackground(new Color(88, 80, 255));
            loginBtn.setForeground(Color.white);
            loginBtn.setFocusPainted(false);
            loginBtn.addActionListener(e -> handleLogin());
            form.add(idField);
            form.add(passField);
            form.add(loginBtn);
            JTextArea demo = new JTextArea();
            demo.setEditable(false);
            demo.setBackground(new Color(250, 250, 250));
            demo.setBorder(BorderFactory.createTitledBorder("Demo Credentials"));
            demo.setText("Admin: ID = admin | Pass = admin123\n\n" + "Voters (no password required for demo):\n" + "voter001\n" + "voter002\n" + "voter003\n" + "voter004");
            demo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            card.add(title);
            card.add(form);
            card.add(Box.createVerticalStrut(20));
            card.add(demo);
            add(card, new GridBagConstraints());
        }
        private void handleLogin() {
            String id = idField.getText();
            String pass = new String(passField.getPassword());
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (controller.adminId.equals(id)) {
                if (controller.adminLogin(id, pass)) {
                    adminPanel.refreshAll();
                    showPanel("admin");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid admin credentials.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Voter v = controller.voters.findById(id);
                if (v == null) {
                    JOptionPane.showMessageDialog(this, "Voter ID not found.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (controller.voterLogin(id, pass)) {
                    voterPortalPanel.setLoggedInVoter(id);
                    voterPortalPanel.refreshCandidates();
                    showPanel("voterPortal");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid voter password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }
    class AdminPanel extends JPanel {
        JTextField candIdField, candNameField, candPartyField;
        JTextField voterIdField, voterNameField, voterPassField, voterConfirmPassField;
        JTextArea displayArea;
        ResultsChartPanel chartPanel;

        public AdminPanel() {
            setLayout(new BorderLayout(12, 12));
            setBackground(new Color(250, 250, 250));
            setBorder(new EmptyBorder(12, 12, 12, 12));

            JPanel top = new JPanel(new BorderLayout());
            JLabel title = new JLabel("Admin Dashboard");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            top.add(title, BorderLayout.WEST);
            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> showPanel("login"));
            top.add(logout, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.setPreferredSize(new Dimension(360, 400));
            JPanel addCandPanel = new JPanel(new GridLayout(4, 1, 6, 6));
            addCandPanel.setBorder(new TitledBorder("Add Candidate"));
            candIdField = new JTextField();
            candIdField.setBorder(new TitledBorder("Candidate ID"));
            candNameField = new JTextField();
            candNameField.setBorder(new TitledBorder("Candidate Name"));
            candPartyField = new JTextField();
            candPartyField.setBorder(new TitledBorder("Party"));
            JButton addCandBtn = new JButton("Add Candidate");
            addCandBtn.addActionListener(e -> addCandidate());
            addCandPanel.add(candIdField);
            addCandPanel.add(candNameField);
            addCandPanel.add(candPartyField);
            addCandPanel.add(addCandBtn);

            JPanel addVoterPanel = new JPanel(new GridLayout(4, 1, 6, 6));
            addVoterPanel.setBorder(new TitledBorder("Add Voter"));
            voterIdField = new JTextField();
            voterIdField.setBorder(new TitledBorder("Voter ID"));
            voterNameField = new JTextField();
            voterNameField.setBorder(new TitledBorder("Voter Name"));
            voterPassField = new JTextField();
            voterPassField.setBorder(new TitledBorder("Password"));
            voterConfirmPassField = new JTextField();
            voterConfirmPassField.setBorder(new TitledBorder("Confirm Password"));

            JButton addVoterBtn = new JButton("Add Voter");
            addVoterBtn.addActionListener(e -> addVoter());
            addVoterPanel.add(voterIdField);
            addVoterPanel.add(voterNameField);
            addVoterPanel.add(voterPassField);
            addVoterPanel.add(voterConfirmPassField);
            addVoterPanel.add(addVoterBtn);
            left.add(addCandPanel);
            left.add(Box.createVerticalStrut(12));
            left.add(addVoterPanel);

            JPanel right = new JPanel(new BorderLayout(8, 8));
            right.setOpaque(false);
            displayArea = new JTextArea(10, 30);
            displayArea.setEditable(false);
            displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.add("Results", new JScrollPane(displayArea));
            tabbedPane.add("Audit Log", new JScrollPane(new JTextArea(controller.logs.allLogs())));
            chartPanel = new ResultsChartPanel();
            chartPanel.setPreferredSize(new Dimension(450, 290));
            chartPanel.setBorder(new TitledBorder("Live Election Results"));
            right.add(chartPanel, BorderLayout.CENTER);
            right.add(tabbedPane, BorderLayout.SOUTH);
            add(left, BorderLayout.WEST);
            add(right, BorderLayout.CENTER);
        }
        private boolean isValidName(String name) {
         try {
             if (name.matches(".*\\d.*"))  // contains numbers
                throw new Exception("Name cannot contain numbers.");
             if (name.trim().isEmpty())
               throw new Exception("Name cannot be empty.");
              return true;
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Name", JOptionPane.ERROR_MESSAGE);
                 return false;
             }
        }

        private void addCandidate() {
            String id = candIdField.getText();
            String name = candNameField.getText();
            String party = candPartyField.getText();

            try {
                if (id.isEmpty() || name.isEmpty() || party.isEmpty())
                    throw new Exception("Please fill all candidate details.");

                if (!isValidName(name)) return;

                controller.addCandidate(id, name, party);
                refreshAll();
                JOptionPane.showMessageDialog(this, "Candidate added successfully.");

                candIdField.setText("");
                candNameField.setText("");
                candPartyField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        private void addVoter() {
            String id = voterIdField.getText(), name = voterNameField.getText(), pass = voterPassField.getText(), confirmPass = voterConfirmPassField.getText();
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Voter ID and Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            controller.addVoter(id, name, pass);
            refreshAll();
            JOptionPane.showMessageDialog(this, "Voter added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            voterNameField.setText("");
            voterPassField.setText("");
        }
        public void refreshAll() {
            displayArea.setText(controller.candidates.resultsText());
            chartPanel.repaint();
            JScrollPane scrollPane = (JScrollPane) ((JTabbedPane) chartPanel.getParent().getComponent(1)).getComponentAt(1);
            ((JTextArea) scrollPane.getViewport().getView()).setText(controller.logs.allLogs());
            voterIdField.setText("voter00" + (controller.voters.count() + 1));
        }
    }
    class VoterPortalPanel extends JPanel {
        String loggedVoterId = null;
        JLabel welcomeLabel;
        JPanel candidatesGrid;
        Candidate selectedCandidate = null;

        public VoterPortalPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            JPanel top = new JPanel(new BorderLayout());
            welcomeLabel = new JLabel();
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            top.add(welcomeLabel, BorderLayout.WEST);
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(e -> {
                loggedVoterId = null;
                showPanel("login");
            });
            top.add(logoutBtn, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);
            candidatesGrid = new JPanel(new GridLayout(0, 2, 12, 12));
            add(new JScrollPane(candidatesGrid), BorderLayout.CENTER);
            JButton castVoteBtn = new JButton("Cast Your Vote");
            castVoteBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            castVoteBtn.setBackground(new Color(30, 180, 100));
            castVoteBtn.setForeground(Color.WHITE);
            castVoteBtn.addActionListener(e -> doCastVote());
            add(castVoteBtn, BorderLayout.SOUTH);
        }
        public void setLoggedInVoter(String id) {
            this.loggedVoterId = id;
            Voter v = controller.voters.findById(id);
            welcomeLabel.setText("E-Voting Portal - Welcome " + (v != null ? v.name + " (ID: " + v.id + ")" : id));
            selectedCandidate = null;
        }
        public void refreshCandidates() {
            candidatesGrid.removeAll();
            Candidate[] allCandidates = controller.candidates.getRawArray();
            for (Candidate c: allCandidates) {
                 if (c != null){
                    JPanel card = createCandidateCard(c);
                    candidatesGrid.add(card);
                 }
            }
            revalidate();
            repaint();
        }
        private JPanel createCandidateCard(Candidate c) {
            JPanel p = new JPanel(new BorderLayout(10, 0));
            p.setBorder(new CompoundBorder(new LineBorder(new Color(220, 220, 220), 2), new EmptyBorder(8, 8, 8, 8)));
            p.setBackground(Color.white);
            JPanel mid = new JPanel(new GridLayout(2, 1));
            mid.setOpaque(false);
            JLabel name = new JLabel(c.name);
            name.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel party = new JLabel(c.party);
            party.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            party.setForeground(Color.DARK_GRAY);
            mid.add(name);
            mid.add(party);
            p.add(mid, BorderLayout.CENTER);
            p.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    for (Component comp: candidatesGrid.getComponents()) {
                        if (comp instanceof JPanel) {
                            ((JPanel) comp).setBorder(new CompoundBorder(new LineBorder(new Color(220, 220, 220), 2), new EmptyBorder(8, 8, 8, 8)));
                            ((JPanel) comp).setBackground(Color.white);
                        }
                    }
                    p.setBorder(new CompoundBorder(new LineBorder(new Color(90, 120, 255), 3), new EmptyBorder(8, 8, 8, 8)));
                    p.setBackground(new Color(230, 235, 255));
                    selectedCandidate = c;
                }
            });
            return p;
        }

        private void doCastVote() {
            if (loggedVoterId == null) {
                JOptionPane.showMessageDialog(this, "Not logged in.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedCandidate == null) {
                JOptionPane.showMessageDialog(this, "Please select a candidate by clicking on their card.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String res = controller.castVote(loggedVoterId, selectedCandidate.id);
            if ("SUCCESS".equals(res)) {
                JOptionPane.showMessageDialog(this, "Vote successfully recorded for " + selectedCandidate.name, "Success", JOptionPane.INFORMATION_MESSAGE);
                adminPanel.refreshAll();
                showPanel("login");
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res, "Voting Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    class ResultsChartPanel extends JPanel {
        ResultsChartPanel() {
            setBackground(Color.white);
        }
        /*Override*/
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            CandidateList list = controller.candidates;
            if (list.size() == 0) {
                g.drawString("No candidates yet.", 20, 30);
                return;
            }
            int w = getWidth(), h = getHeight(), margin = 40;
            int maxVotes = 1;
            Candidate[] rawCandidates = list.getRawArray();
            for (Candidate c: rawCandidates) {
                if(c != null) maxVotes = Math.max(maxVotes, c.votes);
            }
            int barWidth = Math.max(20, (w - 2 * margin) / list.size() - 20);
            int x = margin + 20;
            for (int i = 0; i < list.size(); i++) {
                Candidate c = rawCandidates[i];
                 if (c == null) continue;
                int barH = (int)(((double) c.votes / maxVotes) * (h - 100));
                int y = h - margin - barH;
                g2d.setColor(new Color(86, 120, 255));
                g2d.fillRoundRect(x, y, barWidth, barH, 12, 12);
                g2d.setColor(Color.black);
                drawCenteredString(g, c.name.split(" ")[0], new Rectangle(x, h - margin + 5, barWidth, 20), g.getFont());
                drawCenteredString(g, c.votes + " votes", new Rectangle(x, y - 24, barWidth, 20), g.getFont().deriveFont(Font.BOLD, 12f));
                x += barWidth + 30;
            }
            g.setColor(Color.DARK_GRAY);
            g.drawString("Total votes: " + controller.candidates.totalVotes(), 12, 25);
        }
        private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
            FontMetrics metrics = g.getFontMetrics(font);
            int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
            int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
            g.setFont(font);
            g.drawString(text, x, y);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EVotingGUI().setVisible(true));
    }
}