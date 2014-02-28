package src.coco.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

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
import src.coco.model.CCCompileErrorList;
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
	private CCCompileErrorList list;

	private CDirectory baseDir;
	private CDirectory libDir;
	private PPProjectSet ppProjectSet;

	// default
	public CCGraphFrame(CCCompileErrorList list, CDirectory baseDir,
			CDirectory libDir, PPProjectSet ppProjectSet) {
		this.list = list;
		this.baseDir = baseDir;
		this.libDir = libDir;
		this.ppProjectSet = ppProjectSet;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) (d.width * 0.6);
		height = (int) (d.height * 0.6);
		initialize();
		setGraphAndTable();
	}

	private void setGraphAndTable() {
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.X_AXIS));
		setGraph();
		setSourceTable();
		add(rootPanel);
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		pack();
	}

	private void initialize() {
		// rootPanel.setLayout(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(CCMainFrame2.APP_NAME + " " + CCMainFrame2.VERSION + " - "
				+ list.getMessage() + " �̏ڍ�");
	}

	private void setGraph() {
		// ���{�ꂪ�����������Ȃ��e�[�}
		// ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		// �O���t�f�[�^��ݒ肷��
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < list.getErrors().size(); i++) {
			dataset.addValue(list.getErrors().get(i).getCorrectTime(), "�C������",
					Integer.toString(i + 1));
		}

		// �O���t�̐���
		JFreeChart chart = ChartFactory.createLineChart(list.getMessage()
				+ "�̏C������   ���A�x: " + list.getRare(), "�C����", "�C������", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		// �t�H���g�w�肵�Ȃ��ƕ�����������
		chart.getTitle().setFont(new Font("Font2DHandle", Font.PLAIN, 20));
		chart.getLegend().setItemFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// �w�i�F�̃Z�b�g
		chart.setBackgroundPaint(new CCGraphBackgroundColor().graphColor(list
				.getRare()));

		// TODO: CategoryPlot���p�����ăN���b�N�\�ɂ��Ďg������𑝂₷����
		CategoryPlot plot = chart.getCategoryPlot();

		// y���̐ݒ� �E ���͐����l�݂̂��w���悤�ɂ���
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
		ChartPanel chartpanel = new ChartPanel(chart);
		// chartpanel.setBounds(0, 0, width - 15, height - 40);

		// TODO: TIPS�\������Ȃ�
		JToolTip tooltip = new JToolTip();
		chartpanel.setToolTipText(list.getErrors().size() + " : "
				+ list.getMessage());
		tooltip.setComponent(chartpanel);
		chartpanel.setDisplayToolTips(true);

		rootPanel.add(chartpanel, BorderLayout.WEST);
		// rootPanel.add(chartpanel);
	}

	// TODO ���X�g�����̎���
	private void setSourceTable() {
		// java7����DefaultListModel�Ɋi�[����N���X���w�肵�Ȃ���΂Ȃ�Ȃ�
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (int i = 0; i < list.getErrors().size(); i++) {
			model.addElement((i + 1) + " ��ڂ̏C������ �F "
					+ list.getErrors().get(i).getCorrectTime() + "�b");
		}

		final JList<String> jlist = new JList<String>(model);
		jlist.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// ���N���b�N���ŃI�[�v������
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() >= 2) {
					// �I�����ꂽ�v�f�����X�g�̉��Ԗڂł���̂����擾���C���̎��̃R���p�C���G���[�����擾
					int index = jlist.getSelectedIndex();
					CCCompileError compileError = list.getErrors().get(index);

					// �t�@�C���p�X�ɕK�v�ȗv�f�̎��o��
					String projectname = compileError.getProjectName();
					String filename = compileError.getFilename();

					if (baseDir == null) {
						System.out.println("baseDir null");
						return;
					}

					if (ppProjectSet == null) {
						PPDataManager datamanager = new PPDataManager(baseDir);
						datamanager.setLibDir(libDir);

						CDirectory projectSetDir = datamanager
								.getDataDir()
								.findDirectory(compileError.getProjectSetName());
						ppProjectSet = new PPProjectSet(projectSetDir);
						datamanager.loadProjectSet(ppProjectSet, true, true);
					}

					IPLUnit model = null;
					for (PLProject project : ppProjectSet.getProjects()) {
						if (project.getName().equals(projectname)) {
							// �P�̂̂�
							for (PLFile file : project.getFiles()) {
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

				}
			}
		});

		// TODO �X�N���[���p�l���̃T�C�Y����
		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.getViewport().setView(jlist);

		rootPanel.add(scrollPanel, BorderLayout.EAST);
	}
}