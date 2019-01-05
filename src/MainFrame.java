import java.awt.EventQueue;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import ro.damianteodorbeles.imageprocessing.FilterEngine;
import ro.damianteodorbeles.imageprocessing.Filter;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txfImagePath;

	private BufferedImage image;
	private BufferedImage processedImage;
	private Image scaledImage;
	private Image scaledProcessedImage;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("unchecked")
	public MainFrame() {
		setResizable(false);
		setTitle("Basic Image Processing by Damian-Teodor BELES");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 622, 390);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JButton btnApplyFilter = new JButton("Apply filter");
		JButton btnBrowse = new JButton("Browse...");
		JButton btnSaveImage = new JButton("Save image");
		JComboBox<Filter> cmbFilter = new JComboBox<Filter>();
		JPanel panelFilter = new JPanel();
		JPanel panelProcessedImage = new JPanel();
		JToggleButton tglbtnSeeDefaultOrProcessedImage = new JToggleButton("See default image");

		btnApplyFilter.setBounds(10, 51, 207, 23);
		btnApplyFilter.setEnabled(false);

		btnBrowse.setBounds(517, 7, 89, 23);

		btnSaveImage.setBounds(10, 327, 227, 23);
		btnSaveImage.setEnabled(false);

		cmbFilter.setBounds(10, 20, 207, 20);
		cmbFilter.setEnabled(false);
		cmbFilter.setModel(new EnumComboBoxModel());
		cmbFilter.setRenderer(new EnumComboBoxRenderer());

		panelFilter.setBorder(BorderFactory.createTitledBorder("Filter"));
		panelFilter.setBounds(10, 36, 227, 83);
		panelFilter.setLayout(null);

		panelProcessedImage.setBounds(247, 36, 359, 314);

		tglbtnSeeDefaultOrProcessedImage.setBounds(10, 293, 227, 23);
		tglbtnSeeDefaultOrProcessedImage.setEnabled(false);

		txfImagePath = new JTextField();
		txfImagePath.setBounds(10, 8, 497, 20);
		txfImagePath.setColumns(10);
		txfImagePath.setEditable(false);
		txfImagePath.setHorizontalAlignment(SwingConstants.CENTER);

		btnApplyFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FilterEngine filterEngine = new FilterEngine();
				processedImage = filterEngine.processImage(image, (Filter) cmbFilter.getSelectedItem());
				scaledProcessedImage = processedImage.getScaledInstance(panelProcessedImage.getWidth(),
						panelProcessedImage.getHeight(), BufferedImage.SCALE_SMOOTH);
				panelProcessedImage.getGraphics().drawImage(scaledProcessedImage, 0, 0, null);
				btnSaveImage.setEnabled(true);
				tglbtnSeeDefaultOrProcessedImage.setEnabled(true);
				tglbtnSeeDefaultOrProcessedImage.setSelected(false);
				tglbtnSeeDefaultOrProcessedImage.setText("See default image");
			}
		});

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(
						new FileNameExtensionFilter("Image Files (*.jpg) | (*.gif) | (*.png)", "jpg", "gif", "png"));

				final int dialogResult = fileChooser.showOpenDialog(contentPane);
				if (dialogResult == JFileChooser.APPROVE_OPTION) {
					try {
						File selectedFile = fileChooser.getSelectedFile().getAbsoluteFile();
						image = ImageIO.read(selectedFile);
						scaledImage = image.getScaledInstance(panelProcessedImage.getWidth(),
								panelProcessedImage.getHeight(), BufferedImage.SCALE_SMOOTH);
						txfImagePath.setText(selectedFile.getAbsolutePath());
						panelProcessedImage.getGraphics().drawImage(scaledImage, 0, 0, null);
						btnApplyFilter.setEnabled(true);
						cmbFilter.setEnabled(true);
					} catch (IOException exception) {
						JOptionPane.showMessageDialog(contentPane, "Couldn't read the file containing the image.");
					}
				} else {
					JOptionPane.showMessageDialog(contentPane, "Choose an image in order to proceed.");
				}
			}
		});

		btnSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				final int dialogResult = fileChooser.showOpenDialog(contentPane);
				if (dialogResult == JFileChooser.APPROVE_OPTION) {
					String timestamp = new SimpleDateFormat("dd.MM.yyyy HH.mm").format(new Date());
					try {
						ImageIO.write(processedImage, "PNG",
								new File(fileChooser.getSelectedFile() + "\\Processed Image - " + timestamp + ".png"));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(contentPane, "Couldn't save the image.");
					}
				}
			}
		});

		tglbtnSeeDefaultOrProcessedImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean tglbtnEnabled = tglbtnSeeDefaultOrProcessedImage.isSelected() ? false : true;
				Image imageToDraw = tglbtnSeeDefaultOrProcessedImage.isSelected() ? scaledImage : scaledProcessedImage;
				String tglbtnText = tglbtnSeeDefaultOrProcessedImage.isSelected() ? "See processed image"
						: "See default image";

				panelProcessedImage.getGraphics().drawImage(imageToDraw, 0, 0, null);
				tglbtnSeeDefaultOrProcessedImage.setText(tglbtnText);
				btnSaveImage.setEnabled(tglbtnEnabled);
			}
		});

		panelFilter.add(btnApplyFilter);
		panelFilter.add(cmbFilter);

		contentPane.add(btnBrowse);
		contentPane.add(btnSaveImage);
		contentPane.add(panelFilter);
		contentPane.add(panelProcessedImage);
		contentPane.add(tglbtnSeeDefaultOrProcessedImage);
		contentPane.add(txfImagePath);
	}
}
