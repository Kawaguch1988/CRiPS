package codeblocks;

import renderable.RenderableBlock;

/**
 * <code>SocketRule</code> checks if the two sockets being matched can connect
 * simply by checking if the socket/plug match in kind.
 * 
 */
public class SocketRule implements LinkRule {

	/**
	 * Returns true if the two sockets of the two blocks can link by matching
	 * their socket kind; false if not. Both sockets must be empty to return
	 * true.
	 * 
	 * @param block1
	 *            the associated <code>Block</code> of socket1
	 * @param block2
	 *            the associated <code>Block</code> of socket2
	 * @param socket1
	 *            a <code>Socket</code> or plug of block1
	 * @param socket2
	 *            a <code>Socket</code> or plug of block2
	 * @return true if the two sockets of the two blocks can link; false if not
	 */

	private final String[] colorLeterals = { "blue", "cyan", "green",
			"magenta", "orange", "pink", "red", "white", "yellow", "gray",
			"lightGray", "darkGray", "black", };

	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {

		// �u�y���̐F��ς���v�u���b�N�ɐF�w��̃u���b�N�ȊO�͂���Ȃ��悤�ɂ���B
		if ("color".equals(block1.getGenusName())) {
			if (!resolveColorBlock(block2)) {
				return false;
			}
		}

		if ("color".equals(block2.getGenusName())) {
			if (!resolveColorBlock(block1)) {
				return false;
			}
		}

		// Make sure that none of the sockets are connected, and that exactly one of the sockets is a plug.
		if (socket1.hasBlock()
				|| socket2.hasBlock()
				|| !((block1.hasPlug() && block1.getPlug() == socket1) ^ (block2
						.hasPlug() && block2.getPlug() == socket2))) {
			// arranged by sakai lab 2011/11/21
			return replaceBlockChecker(block1, socket2)
					|| replaceBlockChecker(block2, socket1);
			//			return false;
		}
		// If they both have the same kind, then they can connect
		return (socket1.getKind().equals(socket2.getKind()));
	}

	private boolean resolveColorBlock(Block colorLeteralBlock) {
		String blockGenusName = colorLeteralBlock.getGenusName();
		for (String colorLeteral : colorLeterals) {
			if (blockGenusName.equals(colorLeteral)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMandatory() {
		return false;
	}

	/**
	 * created by sakai lab 2011/11/21
	 * 
	 * @param block
	 * @param socket
	 * @return
	 */
	private boolean replaceBlockChecker(Block block, BlockConnector socket) {
		if (block == null) {// 2012.09.25 #matsuzawa
			return false;
		}
		Block sBlock = Block.getBlock(socket.getBlockID());
		if (sBlock == null) {// 2012.09.25 #matsuzawa
			return false;
		}
		if (block.isBlockForSubstitute()
				&& sBlock.isBlockForSubstitute()
				&& ((block.getPlug().getBlockID() == Block.NULL && sBlock
						.getBlockID() != Block.NULL) || (block.getPlug()
						.getBlockID() != Block.NULL && sBlock.getBlockID() == Block.NULL))
				&& block.getKind().equals(sBlock.getKind())) {
			replaceBlock(block, socket);
			return true;
		}
		return false;
	}

	/**
	 * created by sakai lab 2011/11/21
	 * 
	 * @param block
	 * @param socket
	 */
	private void replaceBlock(Block block, BlockConnector socket) {
		RenderableBlock rb = RenderableBlock.getRenderableBlock(block
				.getBlockID());
		RenderableBlock socketRb = RenderableBlock.getRenderableBlock(socket
				.getBlockID());
		socketRb.setLocation(socketRb.getX() + rb.getBlockWidth(),
				socketRb.getY());
		socketRb.setParentWidget(rb.getParentWidget());
		rb.getParentWidget().addBlock(socketRb);
	}
}