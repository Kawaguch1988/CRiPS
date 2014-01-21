package src.coco.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import src.coco.model.CCCompileError;
import src.coco.model.CCCompileErrorKind;
import clib.common.filesystem.CDirectory;
import clib.common.time.CTime;

public class CCGraphFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width = 680;
	private int height = 560;

	private JPanel rootPanel = new JPanel();
	private CCCompileErrorKind list;

	private CDirectory libDir;
	private CDirectory base;
	private PPProjectSet ppProjectSet;

	private JFreeChart chart;
	private ChartPanel chartpanel;

	private ArrayList<CCSourceCompareViewer> projectviewers = new ArrayList<CCSourceCompareViewer>();

	// default
	public CCGraphFrame(CCCompileErrorKind list, CDirectory libDir,
			CDirectory base, PPProjectSet ppProjectSet) {
		this.list = list;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) (d.width * 0.6);
		height = (int) (d.height * 0.6);
		this.libDir = libDir;
		this.base = base;
		this.ppProjectSet = ppProjectSet;
		initialize();
		setGraphAndList();
	}

	private void initialize() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(CCMainFrame2.APP_NAME + " " + CCMainFrame2.VERSION + " - "
				+ list.getMessage() + " �̏ڍ�");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				projectViewerFrameClose();
			}
		});
	}

	private void setGraphAndList() {
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.X_AXIS));
		setGraphPanel();
		setLeftPanel();
		add(rootPanel);
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		pack();
	}

	private void setLeftPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());

		setChangeGraphRangeComboBox(leftPanel);
		setSourceList(leftPanel);

		rootPanel.add(leftPanel, BorderLayout.WEST);
	}

	// TODO: �R���p�C���G���[�ꗗ�\�̃{�^���̃O���t�Ƃقړ���
	private void setGraphPanel() {
		// ���{�ꂪ�����������Ȃ��e�[�}
		// ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());

		// �O���t�f�[�^��ݒ肷��
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < list.getErrors().size(); i++) {
			dataset.addValue(list.getErrors().get(i).getCorrectionTime(),
					"�C������", Integer.toString(i + 1));
		}

		// �O���t�̐���
		chart = ChartFactory.createLineChart(list.getMessage()
				+ "�̏C������   ���A�x: " + list.getRare(), "�C����", "�C������", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		// �t�H���g�w��i���������΍�j
		chart.getTitle().setFont(new Font("Font2DHandle", Font.PLAIN, 20));
		chart.getLegend().setItemFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// �w�i�F�Z�b�g
		chart.setBackgroundPaint(new CCGraphBackgroundColor().graphColor(list
				.getRare()));

		// TODO: �v���b�g�N���b�N�@�\
		// ���̐ݒ�̊֌W����C���Plot�N���X������
		CategoryPlot plot = chart.getCategoryPlot();

		// y���̐ݒ� �E ���͐����l�̂�
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberAxis.setVerticalTickLabels(false);
		numberAxis.setAutoRangeStickyZero(true);
		numberAxis.setRange(0, 120);
		numberAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// x���̐ݒ�
		CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
		domainAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// �v���b�g�̐ݒ�
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setSeriesPaint(0, ChartColor.RED);
		renderer.setSeriesStroke(0, new BasicStroke(2));
		renderer.setSeriesShapesVisible(0, true);

		// �O���t��JPanel��ɔz�u����
		chartpanel = new ChartPanel(chart);
		rootPanel.add(chartpanel, BorderLayout.WEST);
	}

	private void setChangeGraphRangeComboBox(JPanel panel) {
		String[] labels = { "120�b�Œ胂�[�h", "�O���t�T�`���[�h" };
		final JComboBox<String> comboBox = new JComboBox<String>(labels);

		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CategoryPlot plot = chart.getCategoryPlot();
				NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();

				if (comboBox.getSelectedIndex() == 0) {
					numberAxis.setRange(0, 120);
				} else if (comboBox.getSelectedIndex() == 1) {
					numberAxis.setAutoRange(true);
				} else {
					throw new RuntimeException("�O���t���[�h���I������Ă��܂���");
				}
			}
		});

		panel.add(comboBox, BorderLayout.NORTH);
	}

	// private void setChangeRangeButton(JPanel leftPanel) {
	// final JToggleButton button = new JToggleButton("��������");
	//
	// button.addMouseListener(new MouseAdapter() {
	// Boolean mode = true;
	//
	// @Override
	// public void mouseClicked(MouseEvent e) {
	// CategoryPlot plot = chart.getCategoryPlot();
	// NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
	// if (mode) {
	// numberAxis.setAutoRange(true);
	// mode = false;
	// } else {
	// numberAxis.setRange(0, 120);
	// mode = true;
	// }
	// }
	// });
	//
	// leftPanel.add(button, BorderLayout.NORTH);
	// }

	private void setSourceList(JPanel leftPanel) {
		String[] columnNames = { "�C����", "��������", "�v���O������", "�C������" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		for (int i = 0; i < list.getErrors().size(); i++) {
			String count = String.valueOf(i + 1);
			String time = new CTime(list.getErrors().get(i).getBeginTime())
					.toString();
			String filename = list.getErrors().get(i).getFilenameNoPath();
			String correctTime = String.valueOf(list.getErrors().get(i)
					.getCorrectionTime())
					+ "�b";

			String[] oneTableData = { count, time, filename, correctTime };
			model.addRow(oneTableData);
		}

		// java7����DefaultListModel�Ɋi�[����N���X���w�肵�Ȃ���΂Ȃ�Ȃ�
		// DefaultListModel<String> model = new DefaultListModel<String>();
		// for (int i = 0; i < list.getErrors().size(); i++) {
		// CTime time = new CTime(list.getErrors().get(i).getBeginTime());
		//
		// model.addElement("�������� " + time.toString() + "�F �C������ "
		// + list.getErrors().get(i).getCorrectionTime() + "�b");
		// }
		//
		// final JList<String> jlist = new JList<String>(model);

		final JTable table = new JTable(model);
		table.setDefaultEditor(Object.class, null); // �e�[�u����ҏW�s�ɂ���
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// ���N���b�N���ŃI�[�v������
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() >= 2) {
					// �I�����ꂽ�v�f�����X�g�̉��Ԗڂł���̂����擾���C���̎��̃R���p�C���G���[�����擾
					int index = table.getSelectedRow();
					final CCCompileError compileError = list.getErrors().get(
							index);
					// �t�@�C���p�X�ɕK�v�ȗv�f�̎��o��
					String projectname = compileError.getProjectName();
					String filename = compileError.getFilename();

					// ppProjectSet����������Ă��Ȃ���΁C�����ŏ��� �E ���������O��PPV�����Ƃ��Ȃ��ᖳ��
					if (ppProjectSet == null) {
						PPDataManager ppDataManager = new PPDataManager(base);
						ppDataManager.setLibDir(libDir);

						CDirectory projectSetDir = ppDataManager
								.getDataDir()
								.findDirectory(compileError.getProjectSetName());
						ppProjectSet = new PPProjectSet(projectSetDir);
						ppDataManager.loadProjectSet(ppProjectSet, true, true);
					}

					IPLUnit model = null;
					for (PLProject project : ppProjectSet.getProjects()) {
						if (project.getName().equals(projectname)) {
							// �P�̂̂�
							List<PLFile> files = project.getFiles();
							for (PLFile file : files) {
								if (file.getName().equals(filename)) {
									model = file;
								}
							}

							// ���̃v���W�F�N�g�S��
							// model = project.getRootPackage();
						}
					}

					if (model == null) {
						throw new RuntimeException(
								"�R���p�C���G���[�������̃\�[�X�R�[�h�{���Ɏ��s���܂���");
					}

					final CCSourceCompareViewer frame = new CCSourceCompareViewer(
							model);
					long beginTime = compileError.getBeginTime();
					frame.getTimelinePane().getTimeModel2()
							.setTime(new CTime(beginTime));
					long endTime = compileError.getEndTime();
					frame.getTimelinePane().getTimeModel()
							.setTime(new CTime(endTime));
					// frame.openToggleExtraView();

					frame.setBounds(50, 50, 1000, 700);
					frame.setVisible(true);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							// �C���O�C�ԏC����
							frame.fitScale();
						}
					});

					projectviewers.add(frame);
				}
			}
		});

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.getViewport().setView(table);

		leftPanel.add(scrollPanel, BorderLayout.CENTER);
	}

	public void projectViewerFrameClose() {
		for (CCSourceCompareViewer frame : projectviewers) {
			frame.dispose();
		}
	}
}
