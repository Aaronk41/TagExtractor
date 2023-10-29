import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TagExtractorGUI extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private Map<String, Integer> tagFrequencyMap;
    private Set<String> stopWords;

    public TagExtractorGUI() {
        tagFrequencyMap = new HashMap<>();
        stopWords = loadStopWords("English Stop Words.txt");

        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        textArea = new JTextArea(20, 40);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton openFileButton = new JButton("Open Text File");
        openFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        JButton extractTagsButton = new JButton("Extract Tags");
        extractTagsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });

        JButton saveTagsButton = new JButton("Save Tags");
        saveTagsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTags();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openFileButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void openFile() {
        fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processTextFile(selectedFile);
        }
    }

    private void extractTags() {
        textArea.setText(""); // Clear the text area
        if (tagFrequencyMap.isEmpty()) {
            textArea.append("No tags to extract. Please open a text file.\n");
        } else {
            textArea.append("Tags and Frequencies:\n");
            for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }
    }

    private void saveTags() {
        if (tagFrequencyMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tags to save.");
        } else {
            fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(selectedFile)) {
                    for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                        writer.println(entry.getKey() + ": " + entry.getValue());
                    }
                    JOptionPane.showMessageDialog(this, "Tags saved to " + selectedFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processTextFile(File file) {
        tagFrequencyMap.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
            textArea.setText("Tags extracted from: " + file.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLine(String line) {
        // Tokenize the line and update tagFrequencyMap
        String[] words = line.split("\\s+"); // Split by whitespace
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "").toLowerCase(); // Remove non-letter characters and convert to lowercase
            if (!word.isEmpty() && !stopWords.contains(word)) {
                tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
            }
        }
    }

    private Set<String> loadStopWords(String fileName) {
        Set<String> stopwords = new HashSet<>();
        try (InputStream inputStream = getClass().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopwords.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopwords;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagExtractorGUI gui = new TagExtractorGUI();
            gui.setVisible(true);
        });
    }
}

