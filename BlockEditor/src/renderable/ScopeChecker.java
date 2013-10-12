package renderable;

import java.awt.Color;

import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockLinkChecker;

public class ScopeChecker {

	public boolean checkScope(Block beforeBlock, Block cmpBlock) {
		//callAction�̏ꍇ�́A�Q�ƌ����\�P�b�g�������Ă�̂ŁA���̂��߂̏������K�v
		Block originBlock = beforeBlock;//��������ꏊ�̒��O�̃u���b�N

		String compareBlockName;
		Block compareBlock = cmpBlock;
		if (isCompareBlock(cmpBlock)
				&& !cmpBlock.getGenusName().contains("private")) {
			//���O�̃u���b�N�̃v���O�Ƀu���b�N������ꍇ�́A���ꂪ�{���̃u���b�N
			if (Block.getBlock(beforeBlock.getPlugBlockID()) != null) {
				beforeBlock = purcePlugBlock(beforeBlock);
				originBlock = beforeBlock;
			}

			//���ꏬ���̃u���b�N�̏ꍇ��true��Ԃ�
			if (isIndependentBlock(beforeBlock)) {
				return true;
			}

			//callAction�u���b�N�̏ꍇ�́A�Q�ƃu���b�N�̓\�P�b�g�ɂ������Ă���̂ŁA��������Q�ƃu���b�N�ɐݒ肷��
			if (compareBlock.getGenusName().equals("callActionMethod2")
					|| compareBlock.getGenusName().equals("callGetterMethod2")) {
				compareBlock = Block.getBlock(compareBlock.getSocketAt(0)
						.getBlockID());
				if (compareBlock.getGenusName().contains("private")) {//�Q�ƃu���b�N��private�������ꍇ�̓X�R�[�v��킸
					return true;
				}
			}

			//�Q�ƃu���b�N�̖��O���擾
			compareBlockName = compareBlock.getBlockLabel().substring(
					0,
					compareBlock.getBlockLabel().indexOf(
							compareBlock.getLabelSuffix()));

			if (originBlock == null) {//�T���Ώۂ̃u���b�N�����݂��Ȃ�
				RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
						.setBlockHighlightColor(Color.RED);
				return false;
			}

			//�Q�ƌ���T������
			long originID = searchCompareBlockOrigin(originBlock,
					compareBlockName);

			if (originID == -1) {//�Q�ƌ��̒T���Ɏ��s
				originBlock = cmpBlock;
				originBlock = purcePlugBlock(originBlock);
				while (Block.getBlock(originBlock.getAfterBlockID()) != null) {
					originBlock = Block.getBlock(originBlock.getAfterBlockID());
				}
				//�����̎����Ă�u���b�N�̒���T��
				originID = searchCompareBlockOrigin(originBlock,
						compareBlockName);
				//���s
				if (originID == -1) {
					RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
							.setBlockHighlightColor(Color.RED);
					return false;
				}
				beforeBlock = originBlock;
			}

			originBlock = Block.getBlock(originID);//�Q�ƌ��u���b�N
			//�X�R�[�v�`�F�b�N����u���b�N�̏�������e��T��
			Block compareBlockParent = Block
					.getBlock(searchParentBlockID(RenderableBlock
							.getRenderableBlock(beforeBlock.getBlockID())));

			if (compareBlockParent == null) {//�T�����s
				RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
						.setBlockHighlightColor(Color.RED);
				return false;
			}

			//�����ł���ꏊ���ǂ����m�F����
			if (confirmCompareBlockIsBelongable(originBlock, compareBlockParent)) {
				return true;
			}

			RenderableBlock.getRenderableBlock(cmpBlock.getBlockID())
					.setBlockHighlightColor(Color.RED);

			return false;
		} else {//�Q�ƃu���b�N�łȂ������ꍇ�Aprivate�ϐ��̎Q�ƃu���b�N�ŉ�����ꍇ�͌�������
			return true;
		}
	}

	private Block purcePlugBlock(Block block) {

		while (Block.getBlock(block.getPlugBlockID()) != null) {
			block = Block.getBlock(block.getPlugBlockID());
		}
		return block;
	}

	private long searchCompareBlockOrigin(Block originBlock,
			String compareBlockName) {
		while (!originBlock.getGenusName().equals("procedure")) {
			if (originBlock.getBlockLabel().equals(compareBlockName)) {//���x�����m�F���āA��v���邩�@
				return originBlock.getBlockID();
			}

			if (originBlock.getGenusName().equals("abstraction")) {
				long result = searchCompareOriginBlockInAbstructionBlock(
						Block.getBlock(originBlock.getSocketAt(0).getBlockID()),
						compareBlockName);
				if (result != -1) {
					return result;
				}
			}

			originBlock = Block.getBlock(originBlock.getBeforeBlockID());//�u���b�N�X�V

			if (originBlock == null) {//�I������
				return -1;
			}
		}
		//procedure�܂ŒH�蒅������A�����u���b�N���`�F�b�N�@
		for (BlockConnector socket : BlockLinkChecker
				.getSocketEquivalents(originBlock)) {
			if (Block.getBlock(socket.getBlockID()) == null) {
				break;
			}

			if (Block.getBlock(socket.getBlockID()).getBlockLabel()
					.equals(compareBlockName)) {//���x�����m�F���āA��v���邩�@��v������break;
				return originBlock.getBlockID();//�֋X��proceure�u���b�N��Ԃ�
			}
		}
		return -1;//procedure�܂ŒH�蒅������A�Q�ƌ��̒T���Ɏ��s
	}

	private long searchCompareOriginBlockInAbstructionBlock(Block start,
			String compareBlockName) {
		if (start == null) {
			return -1;
		}
		while (!start.getBlockLabel().equals(compareBlockName)) {
			start = Block.getBlock(start.getAfterBlockID());
			if (start == null) {
				return -1;
			}
		}
		return start.getBlockID();
	}

	//�Q�ƃu���b�N�������ł��邩�ǂ����m�F����. �@�Q�ƌ��̃u���b�N�̏�������e���牺�����ǂ��Ă����āA�X�R�[�v������Ă��邩�ǂ����m�F����
	private boolean confirmCompareBlockIsBelongable(Block originBlock,
			Block compareBlockParent) {
		//�e���牺�����ǂ��Ă���
		for (Block block = Block.getBlock(searchParentBlockID(RenderableBlock
				.getRenderableBlock(originBlock.getBlockID()))); block != null; block = Block
				.getBlock(block.getAfterBlockID())) {
			if (compareBlockParent.getBlockID() == block.getBlockID()) {
				return true;
			}
			if (block.hasStubs()) {
				if (searchStubs(block, compareBlockParent.getBlockID())) {
					return true;
				}
			}
			//�u���b�N���\�P�b�g�������Ă����ꍇ�́A���̃\�P�b�g�����m�F����B
			if (block.getSockets() != null) {//�\�P�b�g�Ƀu���b�N�������Ă���
				if (confirmCompareBlockIsBelongableAtSockets(block,
						compareBlockParent)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean searchStubs(Block block, long compareBlockParentID) {
		for (BlockConnector socket : block.getSockets()) {
			if (socket.getBlockID() == compareBlockParentID) {
				return true;
			}
		}
		return false;
	}

	//�u���b�N�������ł��邩�ǂ����AabstractionBlock�u���b�N�������ׂĒT������B
	private boolean confirmCompareBlockIsBelongableAtSockets(Block originBlock,
			Block compareBlockParent) {
		for (BlockConnector socket : BlockLinkChecker
				.getSocketEquivalents(originBlock)) {
			for (Block block = Block.getBlock(socket.getBlockID()); block != null; block = Block
					.getBlock(block.getAfterBlockID())) {
				if (compareBlockParent.getBlockID() == block.getBlockID()) {//
					return true;
				}
				if (block.getSockets() != null) {//�\�P�b�g�Ƀu���b�N�������Ă���
					if (confirmCompareBlockIsBelongableAtSockets(block,
							compareBlockParent)) {
						return true;
					}
				}
			}//abstruction�u���b�N���̂��ׂẴu���b�N��T�����I����			
		}
		return false;
	}

	//�e�̃u���b�N��T������
	private long searchParentBlockID(RenderableBlock prevRBlock) {
		//�Ŋ���procedure,abstruction�u���b�N��T��
		if (prevRBlock == null
				|| prevRBlock.getBlock().getAfterBlockID() == null) {
			return -1;
		}

		RenderableBlock checkRBlock = RenderableBlock
				.getRenderableBlock(prevRBlock.getBlock().getBeforeBlockID());
		Block prevBlock = Block.getBlock(prevRBlock.getBlockID());//���O�̃u���b�N���Ƃ��Ƃ�

		if (checkRBlock == null) {
			if (prevBlock.getGenusName().equals("procedure")) {
				return prevBlock.getBlockID();
			}
			return -1;
		}

		while (Block.getBlock(checkRBlock.getBlock().getBeforeBlockID()) != null) {//���O�u���b�N��null�ɂȂ�܂�
			//abstruction�̏ꍇ�A�e��������Ȃ��̂Ń`�F�b�N
			if (checkRBlock.getGenus().equals("abstraction")) {
				if (Block.getBlock(checkRBlock.getBlockID()).getSocketAt(0)
						.getBlockID().equals(prevBlock.getBlockID())) {//�\�P�b�g�����O�̃u���b�N�ƈ�v�����ꍇ�A
					return prevRBlock.getBlockID();
				}
			}
			//prevBlock�̍X�V
			prevBlock = checkRBlock.getBlock();
			//checkRBlock�̍X�V
			checkRBlock = RenderableBlock.getRenderableBlock(Block.getBlock(
					checkRBlock.getBlockID()).getBeforeBlockID());
		}

		return checkRBlock.getBlockID();
	}

	public static boolean isCompareBlock(Block block) {
		if (block.getGenusName().startsWith("getter")
				|| block.getGenusName().startsWith("setter")
				|| block.getGenusName().startsWith("inc")
				|| block.getGenusName().equals("callActionMethod2")
				|| block.getGenusName().equals("callGetterMethod2")) {
			return true;
		}
		return false;
	}

	public static boolean isIndependentBlock(Block block) {
		while (!block.getGenusName().equals("procedure")) {
			//���̃u���b�N�����݃V�Ȃ��ꍇ�́A���ꏬ���̃u���b�N�̂���true
			if (Block.getBlock(block.getBeforeBlockID()) == null) {
				return true;
			}
			block = Block.getBlock(block.getBeforeBlockID());
		}
		return false;
	}

	public static boolean isAloneBlock(Block block) {
		if (isIndependentBlock(block)
				&& Block.getBlock(block.getAfterBlockID()) == null) {
			return true;
		}
		return false;
	}
}
