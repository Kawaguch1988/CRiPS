/*
 * SContextMenuProvider.java
 * Created on 2011/11/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package a.slab.blockeditor.extent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import renderable.BlockUtilities;
import renderable.RenderableBlock;
import workspace.Workspace;
import workspace.WorkspaceEvent;
import workspace.WorkspaceWidget;
import bc.j2b.model.ElementModel;
import codeblocks.Block;
import codeblocks.BlockLink;

/**
 * @author macchan
 */
public class SContextMenuProvider {

	private RenderableBlock rb;

	private JMenuItem blockCopyItem;
	private JMenuItem createValueItem;
	private JMenuItem createWriterItem;
	private JMenuItem createIncrementerItem;
	private JMenuItem createCallActionMethodBlockItem;
	private JMenuItem createCallGetterMethodBlockItem;
	private JMenuItem createCallDoubleMethodBlockItem;
	private JMenuItem createCallBooleanMethodBlockItem;
	private JMenuItem createCallStringMethodBlockItem;
	private JMenuItem createCallerItem;

	public SContextMenuProvider(RenderableBlock rb) {
		this.rb = rb;
	}

	private JMenuItem createBlockCopyMenu() {
		if (blockCopyItem == null) {
			blockCopyItem = new JMenuItem("����");
			blockCopyItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SBlockCopier(rb).doWork(e);
				}
			});
		}
		return blockCopyItem;
	}

	private JMenuItem createCreateValueMenu() {
		if (createValueItem == null) {
			createValueItem = new JMenuItem("�u�l�u���b�N�v�̍쐬");
			createValueItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("getter", rb).doWork(e);
				}
			});
		}
		return createValueItem;
	}

	// #ohata added
	private JMenuItem createNewGetterMenu() {
		JMenuItem item = new JMenuItem("�Q�b�^�[���\�b�h�̍쐬");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewGetterMethod("procedure");
			}
		});
		return item;
	}

	// #ohata added
	private JMenuItem createNewSetterMenu() {
		JMenuItem item = new JMenuItem("�Z�b�^�[���\�b�h�̍쐬");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewSetterMethod("procedure");
			}
		});
		return item;
	}

	private JMenuItem createCreateWriterMenu() {
		if (createWriterItem == null) {
			createWriterItem = new JMenuItem("�u�����u���b�N�v�̍쐬");
			createWriterItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("setter", rb).doWork(e);
				}
			});
		}
		return createWriterItem;
	}

	private JMenuItem createCreateIncrementerMenu() {
		if (createIncrementerItem == null) {
			createIncrementerItem = new JMenuItem("�u���₷�u���b�N�v�̍쐬");
			createIncrementerItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("inc", rb).doWork(e);
				}
			});
		}
		return createIncrementerItem;
	}

	private JMenuItem createCallActionMethodBlockMenu() {
		if (createCallActionMethodBlockItem == null) {
			createCallActionMethodBlockItem = new JMenuItem("�u���\�b�h���s�u���b�N�v�̍쐬");
			createCallActionMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callActionMethod", rb).doWork(e);
						}
					});
		}
		return createCallActionMethodBlockItem;
	}

	private JMenuItem createCallGetterMethodBlockMenu() {
		if (createCallGetterMethodBlockItem == null) {
			createCallGetterMethodBlockItem = new JMenuItem(
					"�u���\�b�h���s�u���b�N(�����^)�v�̍쐬");
			createCallGetterMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callGetterMethod", rb).doWork(e);
						}
					});
		}
		return createCallGetterMethodBlockItem;
	}

	private JMenuItem createCallDoubleMethodBlockMenu() {
		if (createCallDoubleMethodBlockItem == null) {
			createCallDoubleMethodBlockItem = new JMenuItem(
					"�u���\�b�h���s�u���b�N(double�^)�v�̍쐬");
			createCallDoubleMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callDoubleMethod", rb).doWork(e);
						}
					});
		}
		return createCallDoubleMethodBlockItem;
	}

	private JMenuItem createCallBooleanMethodBlockMenu() {
		if (createCallBooleanMethodBlockItem == null) {
			createCallBooleanMethodBlockItem = new JMenuItem(
					"�u���\�b�h���s�u���b�N(�^�U�^)�v�̍쐬");
			createCallBooleanMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callBooleanMethod", rb).doWork(e);
						}
					});
		}
		return createCallBooleanMethodBlockItem;
	}

	private JMenuItem createCallStringMethodBlockMenu() {
		if (createCallStringMethodBlockItem == null) {
			createCallStringMethodBlockItem = new JMenuItem(
					"�u���\�b�h���s�u���b�N(������)�v�̍쐬");
			createCallStringMethodBlockItem
					.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							new SStubCreator("callStringMethod", rb).doWork(e);
						}
					});
		}
		return createCallStringMethodBlockItem;
	}

	private JMenuItem createCallerMenu() {
		if (createCallerItem == null) {
			createCallerItem = new JMenuItem("�u���\�b�h���s�u���b�N�v�̍쐬");
			createCallerItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("caller", rb).doWork(e);
				}
			});
		}
		return createCallerItem;
	}

	public JMenu createClassMethodsCategory(String className,
			List<Map<String, List<String>>> methods) {
		JMenu category = new JMenu(className);
		for (Map<String, List<String>> method : methods) {
			category.add(createCallClassMethodMenu(method));
		}

		return category;
	}

	/**
	 * @return
	 */
	public JPopupMenu getPopupMenu() {

		JPopupMenu menu = new JPopupMenu();
		// #ohata added
		if (rb.getBlock().isPrivateVariableBlock()) {
			menu.add(createCreateValueMenu());
			menu.add(createNewGetterMenu());
			menu.add(createCreateWriterMenu());
			menu.add(createNewSetterMenu());
			//menu.add(createCreateGetterMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isVariableDeclBlock()
		/*&& !rb.getBlock().isObjectTypeVariableDeclBlock()*/) {
			menu.add(createCreateValueMenu());
			menu.add(createCreateWriterMenu());
			//menu.add(createCreateGetterMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isProcedureParamBlock()) {
			menu.add(createCreateValueMenu());
			menu.add(createCreateWriterMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().isNumberVariableDecBlock()) {
			menu.add(createCreateIncrementerMenu());
			menu.addSeparator();
		}

		if (rb.getBlock().getGenusName().contains("arrayobject")) {//�z��
			final String scope = rb.getBlock().getGenusName()
					.substring(0, rb.getBlock().getGenusName().indexOf("-"));

			final String type = getBlockVariableType(rb.getBlock()
					.getGenusName());

			//�^�ɉ������Q�b�^�[�A�Z�b�^�[�̒ǉ�
			JMenuItem elementGetter = new JMenuItem("�u�����u���b�N�i�v�f�j�v�̍쐬");
			//getter�̍쐬
			elementGetter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("setter-" + scope + "-" + type
							+ "arrayelement", rb).doWork(e);
				}
			});
			menu.add(elementGetter);

			//setter
			JMenuItem elementSetter = new JMenuItem("�u�l�u���b�N�i�v�f�j�v�̍쐬");
			elementSetter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new SStubCreator("getter-" + scope + "-" + type
							+ "arrayelement", rb).doWork(e);
				}
			});

			menu.add(elementSetter);

		}

		if (rb.getBlock().isObjectTypeVariableDeclBlock()
				|| rb.getBlock().getGenusName().contains("listobject")) {
			menu.add(createActionBlockMenu());
			menu.add(createGetterBlockMenu());

			//TODO menu�Ƀ��\�b�h��ǉ�
			for (String key : rb.getMethods().keySet()) {
				menu.add(createClassMethodsCategory(key,
						rb.getMethods().get(key)));
			}

			if (rb.getBlock().getHeaderLabel().contains("Scanner")) {
				{
					JMenu category = new JMenu("Scanner");
					category.add(createCallMethodMenu("next", "���͂��󂯎��(������^)"));
					category.add(createCallMethodMenu("nextInt", "���͂��󂯎��(�����^)"));
					category.add(createCallMethodMenu("nextDouble",
							"���͂��󂯎��(�����^)"));
					menu.add(category);
				}
			}
			if (rb.getBlock().getHeaderLabel().contains("Turtle")) {
				{
					JMenu category = new JMenu("Turtle��{");
					category.add(createCallMethodMenu("fd", "�i��"));
					category.add(createCallMethodMenu("bk", "�߂�"));
					category.add(createCallMethodMenu("rt", "���i�E�j"));
					category.add(createCallMethodMenu("lt", "���i���j"));
					category.add(createCallMethodMenu("up", "�y�����グ��"));
					category.add(createCallMethodMenu("down", "�y�������낷"));
					category.add(createCallMethodMenu("color", "�y���F��ς���"));
					menu.add(category);
				}
				{
					JMenu category = new JMenu("Turtle���p");
					category.add(createCallMethodMenu("warp", "���[�v����"));
					category.add(createCallMethodMenu("size", "�傫����ς���"));
					category.add(createCallMethodMenu("getX", "X���W"));
					category.add(createCallMethodMenu("getY", "Y���W"));
					category.add(createCallMethodMenu("getWidth", "��"));
					category.add(createCallMethodMenu("getHeight", "����"));
					category.add(createCallMethodMenu("intersects",
							"�I�u�W�F�N�g���d�Ȃ��Ă��邩�ǂ������ׂ�"));
					category.add(createCallMethodMenu("contains",
							"�I�u�W�F�N�g���w�肵�����W���܂ވʒu�ɂ��邩���ׂ�"));
					category.add(createCallMethodMenu("looks", "�����ڂ�ς���"));
					category.add(createCallMethodMenu("show", "�\������"));
					category.add(createCallMethodMenu("hide", "��\���ɂ���"));
					category.add(createCallMethodMenu("setShow", "�\����Ԃ�ݒ肷��"));
					category.add(createCallMethodMenu("isShow",
							"�\������Ă��邩�ǂ������ׂ�"));
					menu.add(category);
				}
			}
			if (rb.getBlock().getHeaderLabel().contains("ImageTurtle")) {
				JMenu category = new JMenu("ImageTurtle");
				category.add(createCallMethodMenu("image", "�摜�t�@�C����ݒ肷��"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("TextTurtle")) {
				JMenu category = new JMenu("TextTurtle");
				category.add(createCallMethodMenu("text", "�e�L�X�g��ݒ肷��"));
				category.add(createCallMethodMenu("getText", "�e�L�X�g���擾����"));
				category.add(category);
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("SoundTurtle")) {
				JMenu category = new JMenu("TextTurtle");
				category.add(createCallMethodMenu("file", "�t�@�C����ݒ肷��"));
				category.add(createCallMethodMenu("play", "�Đ�����"));
				category.add(createCallMethodMenu("loop", "���[�v�Đ�����"));
				category.add(createCallMethodMenu("stop", "��~����"));
				category.add(createCallMethodMenu("isPlaying", "�Đ����Ă��邩�ǂ���"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("ListTurtle")) {
				JMenu category = new JMenu("ListTurtle");
				category.add(createCallMethodMenu("get", "x�Ԓl�̗v�f�擾"));
				category.add(createCallMethodMenu("getSize", "�v�f��"));
				category.add(createCallMethodMenu("add", "�ǉ�����"));
				category.add(createCallMethodMenu("addFirst", "�ŏ��ɒǉ�����"));
				category.add(createCallMethodMenu("addLast", "�Ō�ɒǉ�����"));
				category.add(createCallMethodMenu("addAll", "�S�Ēǉ�����"));
				category.add(createCallMethodMenu("moveAllTo", "�S�Ĉړ�����"));
				category.add(createCallMethodMenu("removeFirst", "�擪�v�f���폜����"));
				category.add(createCallMethodMenu("removeLast", "�����v�f���폜����"));
				category.add(createCallMethodMenu("removeAll", "�S�Ă̗v�f���폜����"));
				category.add(createCallMethodMenu("getCursor", "�J�[�\���ʒu"));
				category.add(createCallMethodMenu("setCursor", "�J�[�\���ʒu��ݒ肷��"));
				category.add(createCallMethodMenu("moveCursorToNext",
						"�J�[�\����i�߂�"));
				category.add(createCallMethodMenu("moveCursorToPrevious",
						"�J�[�\����߂�"));
				category.add(createCallMethodMenu("getObjectAtCursor",
						"�J�[�\���ʒu�̗v�f�擾"));
				category.add(createCallMethodMenu("addToBeforeCursor",
						"�J�[�\���̑O�ɒǉ�����"));
				category.add(createCallMethodMenu("addToAfterCursor",
						"�J�[�\���̌�ɒǉ�����"));
				category.add(createCallMethodMenu("removeAtCursor",
						"�J�[�\���ʒu�̗v�f���폜����"));
				category.add(createCallMethodMenu("shuffle", "����������"));
				category.add(createCallMethodMenu("warpByTopLeft",
						"�i����̍��W�w��Łj���[�v����"));
				menu.add(category);
			}

			if (rb.getBlock().getGenusName().contains("listobject")) {
				JMenu category = new JMenu("List");
				category.add(createCallListMethodMenu("get", "x�Ԓl�̗v�f�擾"));
				category.add(createCallListMethodMenu("size", "�v�f��"));
				category.add(createCallListMethodMenu("add", "�ǉ�����"));
				category.add(createCallListMethodMenu("clear", "�S�Ă̗v�f���폜����"));
				category.add(createCallListMethodMenu("contains",
						"���X�g������v�f�����邩���ׂ�"));
				category.add(createCallListMethodMenu("isEmpty", "���X�g���󂩒��ׂ�"));
				category.add(createCallListMethodMenu("remove",
						"�w�肵���ԍ��̗v�f���폜����"));
				menu.add(category);
			}

			if (rb.getBlock().getHeaderLabel().contains("CardTurtle")) {
				JMenu category = new JMenu("CardTurtle");
				category.add(createCallMethodMenu("getNumber", "�ԍ��擾"));
				category.add(createCallMethodMenu("getText", "������擾"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("ButtonTurtle")) {
				JMenu category = new JMenu("ButtonTurtle");
				category.add(createCallMethodMenu("isClicked", "�N���b�N���ꂽ���ǂ���"));
				menu.add(category);
			}
			if (rb.getBlock().getHeaderLabel().contains("InputTurtle")) {
				JMenu category = new JMenu("InputTurtle");
				category.add(createCallMethodMenu("getText", "��������擾����"));
				category.add(createCallMethodMenu("text", "�������ݒ肷��"));
				category.add(createCallMethodMenu("clearText", "���������ɂ���"));
				category.add(createCallMethodMenu("setActive", "������Ԃ�ݒ肷��"));
				category.add(createCallMethodMenu("isActive", "������Ԃ��擾����"));
				category.add(createCallMethodMenu("toJapaneseMode", "���{�ꃂ�[�h�ɂ���"));
				category.add(createCallMethodMenu("toEnglishMode", "�p�ꃂ�[�h�ɂ���"));
				category.add(createCallMethodMenu("fontsize", "�t�H���g�T�C�Y��ݒ肷��"));
				menu.add(category);
			}
			menu.addSeparator();
		}

		if (rb.getBlock().isProcedureDeclBlock()) {
			menu.add(createCallerMenu());
			menu.addSeparator();
		}

		if (!rb.getBlock().isProcedureDeclBlock()) {
			menu.add(createBlockCopyMenu());
			menu.addSeparator();
		}

		//�Â��I�u�W�F�N�g���s�u���b�N�̌݊����̂��߂Ɏc���Ă���܂��D
		if (rb.getBlock().isObjectTypeVariableDeclBlock()) {
			menu.add(createCallActionMethodBlockMenu());
			menu.add(createCallGetterMethodBlockMenu());
			menu.add(createCallDoubleMethodBlockMenu());
			menu.add(createCallBooleanMethodBlockMenu());
			menu.add(createCallStringMethodBlockMenu());
			menu.addSeparator();
		}
		return menu;
	}

	private JMenuItem createActionBlockMenu() {
		JMenuItem item = new JMenuItem("�u���s�v�u���b�N�쐬");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createActionGetterBlock(rb, "callActionMethod2");
			}
		});
		return item;
	}

	private JMenuItem createGetterBlockMenu() {
		JMenuItem item = new JMenuItem("�u���s�l�v�u���b�N�쐬");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createActionGetterBlock(rb, "callGetterMethod2");
			}
		});
		return item;
	}

	private JMenuItem createCallClassMethodMenu(
			final Map<String, List<String>> method) {

		String param = "(";
		for (int i = 0; i < method.get("parameters").size(); i++) {
			param += method.get("parameters").get(i);
			if (i + 1 != method.get("parameters").size()) {
				param += ", ";
			}
		}

		param += ")";
		JMenuItem item = new JMenuItem(method.get("name").get(0) + param);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCallMethod(method.get("name").get(0));
			}
		});
		return item;
	}

	/**
	 */
	private JMenuItem createCallMethodMenu(final String name, String label) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCallMethod(name);
			}
		});
		return item;
	}

	private JMenuItem createCallListMethodMenu(final String name, String label) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createListMethod(name);
			}
		});
		return item;
	}

	private void createCallMethod(String name) {
		//RenderableBlock createRb = BlockUtilities.getBlock("get","hoge");//does not work !!

		RenderableBlock newCommandRBlock = createNewBlock(rb.getParentWidget(),
				name);

		boolean cmd = newCommandRBlock.getBlock().getPlug() == null;
		if (cmd) {
			RenderableBlock newActionRBlock = createActionGetterBlock(rb,
					"callActionMethod2");
			connectByBefore(newActionRBlock, 1, newCommandRBlock);
		} else {
			RenderableBlock newGetterRBlock = createActionGetterBlock(rb,
					"callGetterMethod2");
			connectByPlug(newGetterRBlock, 1, newCommandRBlock);

			boolean returnObject = newCommandRBlock.getBlock().getPlug()
					.getKind().equals("object");
			if (returnObject) {
				RenderableBlock newActionRBlock = createNewBlock(
						rb.getParentWidget(), "callActionMethod2");
				newActionRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // �V������������u���b�N�̃|�W�V����
				connectByPlug(newActionRBlock, 0, newGetterRBlock);
			}
		}
	}

	private void createListMethod(String name) {
		RenderableBlock newCommandRBlock = createNewBlock(rb.getParentWidget(),
				name);

		//List�̌^���m�F����
		Block newBlock = Block.getBlock(rb.getBlock().getSocketAt(0)
				.getBlockID());
		Block typeBlock = Block.getBlock(newBlock.getSocketAt(0).getBlockID());
		//list�̌^
		String type = ElementModel.getConnectorType(typeBlock.getBlockLabel());
		if (newCommandRBlock.getBlock().getGenusName().equals("add")) {//�v�f��ǉ����郁�\�b�h�̏ꍇ�́A�v���O�̌`��ς���
			newCommandRBlock.getBlock().getSocketAt(0).setKind(type);
		}
		boolean cmd = newCommandRBlock.getBlock().getPlug() == null;
		if (cmd) {
			RenderableBlock newActionRBlock = createActionGetterBlock(rb,
					"callActionMethod2");
			connectByBefore(newActionRBlock, 1, newCommandRBlock);

		} else {
			RenderableBlock newGetterRBlock = createActionGetterBlock(rb,
					"callGetterMethod2");
			connectByPlug(newGetterRBlock, 1, newCommandRBlock);

			boolean returnObject = newCommandRBlock.getBlock().getPlug()
					.getKind().equals("object");
			if (returnObject) {

				newGetterRBlock.getBlock().setPlugKind(type);

				//�v�f��object�^�Ȃ�AcallActionMethod2�u���b�N�ƌ�������
				if (type.equals("object")) {
					RenderableBlock newActionRBlock = createNewBlock(
							rb.getParentWidget(), "callActionMethod2");
					newActionRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // �V������������u���b�N�̃|�W�V����
					connectByPlug(newActionRBlock, 0, newGetterRBlock);
				}
			}
		}

	}

	//#ohata
	private void createNewGetterMethod(String name) {

		RenderableBlock newCommandRBlock = createNewBlock(rb.getParentWidget(),
				name);
		//procedure�̃u���b�N����ς���
		Block methodBlock = newCommandRBlock.getBlock();
		methodBlock.setBlockLabel("get"
				+ rb.getKeyword().toUpperCase().charAt(0)
				+ rb.getKeyword().substring(1));

		RenderableBlock returnBlock = createNewBlock(rb.getParentWidget(),
				"return");
		RenderableBlock getter = SStubCreator.createStub("getter", rb);

		newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // �V������������u���b�N�̃|�W�V����

		returnBlock.setLocation(rb.getX() + 20,
				rb.getY() + newCommandRBlock.getHeight() + 20); //���������W�w��

		getter.setLocation(rb.getX() + returnBlock.getBlockWidth() + 10,
				rb.getY() + newCommandRBlock.getHeight() + 20);
		//return�ƒl������
		connectByPlug(returnBlock, 0, getter);

		BlockLink link = newCommandRBlock.getNearbyLink();

		if (link != null) {
			link.connect();
		}
	}

	private void createNewSetterMethod(String name) {//#ohata
		RenderableBlock newCommandRBlock = createNewBlock(rb.getParentWidget(),
				name);
		newCommandRBlock.setLocation(rb.getX() + 20, rb.getY() + 20); // �V������������u���b�N�̃|�W�V����
		//���x�����ւ�
		Block methodBlock = newCommandRBlock.getBlock();
		methodBlock.setBlockLabel("set"
				+ rb.getKeyword().toUpperCase().charAt(0)
				+ rb.getKeyword().substring(1));

		RenderableBlock setter = SStubCreator.createStub("setter", rb);
		setter.setLocation(rb.getX() + 20, rb.getY() + 40);

		BlockLink link = newCommandRBlock.getNearbyLink();

		link.connect();

		if (rb.getGenus().endsWith("string")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-string");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("boolean")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-boolean");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("double-number")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-double-number");
			connectByPlug(newCommandRBlock, 0, param);
		} else if (rb.getGenus().endsWith("number")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-number");
			connectByPlug(newCommandRBlock, 0, param);

		} else if (rb.getGenus().endsWith("TextTurtle")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-TextTurtle");
			connectByPlug(newCommandRBlock, 0, param);

		} else if (rb.getGenus().endsWith("Turtle")) {
			RenderableBlock param = createNewBlock(rb.getParentWidget(),
					"proc-param-Tertle");
			connectByPlug(newCommandRBlock, 0, param);
		}

		//		newCommandRBlock.getParentWidget().blockDropped(newCommandRBlock);
		//	newCommandRBlock.getParentWidget().blockDropped(setter);
	}

	private static RenderableBlock createActionGetterBlock(
			RenderableBlock parent, String genusName) {

		RenderableBlock newCallRBlock = createNewBlock(
				parent.getParentWidget(), genusName);

		newCallRBlock.setLocation(parent.getX() + 20, parent.getY() + 20); // �V������������u���b�N�̃|�W�V����

		String genusNameLabel = "getter";
		if (parent.getGenus().contains("private")) {
			genusNameLabel += "private";
		}

		RenderableBlock newValueBlock = SStubCreator.createStub(genusNameLabel,
				parent);

		connectByPlug(newCallRBlock, 0, newValueBlock);

		return newCallRBlock;
	}

	public static RenderableBlock createNewBlock(WorkspaceWidget widget,
			String genusName) {
		for (RenderableBlock block : Workspace.getInstance()
				.getFactoryManager().getBlocks()) {
			if (block.getBlock().getGenusName().equals(genusName)) {
				RenderableBlock newBlock = BlockUtilities.cloneBlock(block
						.getBlock());
				newBlock.setParentWidget(widget);
				widget.addBlock(newBlock);
				return newBlock;
			}
		}
		throw new RuntimeException("block not found: " + genusName);
	}

	public static void connectByPlug(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getBlock(),
				child.getBlock(), parent.getBlock().getSocketAt(socketIndex),
				child.getBlock().getPlug());
		link.connect();
		//��������Ȃ��ƌ`���ς��Ȃ�
		Workspace.getInstance().notifyListeners(
				new WorkspaceEvent(parent.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));
	}

	public static void connectByBefore(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getBlock(),
				child.getBlock(), parent.getBlock().getSocketAt(socketIndex),
				child.getBlock().getBeforeConnector());
		link.connect();
		//��������Ȃ��ƌ`���ς��Ȃ�
		Workspace.getInstance().notifyListeners(
				new WorkspaceEvent(parent.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));
	}

	public static void connectBySocket(RenderableBlock parent, int socketIndex,
			RenderableBlock child) {
		BlockLink link = BlockLink.getBlockLink(parent.getBlock(),
				child.getBlock(), parent.getBlock().getSocketAt(socketIndex),
				child.getBlock().getSocketAt(socketIndex));
		link.connect();
		//��������Ȃ��ƌ`���ς��Ȃ�
		Workspace.getInstance().notifyListeners(
				new WorkspaceEvent(parent.getParentWidget(), link,
						WorkspaceEvent.BLOCKS_CONNECTED));
	}

	private String getBlockVariableType(String name) {
		if (name.contains("number") || name.contains("int")) {
			return "number";
		}

		if (name.contains("String") || name.contains("string")) {
			return "string";
		}

		if (name.contains("double")) {
			return "double";
		}

		return "object";
	}

}
