/*
 * �l�p����ׂĂ����v���O����
 * 2003/06/08
 * Yoshiaki Matsuzawa
 */
public class HandredBlock extends Turtle {

	// �N������
	public static void main(String[] args) {
		Turtle.startTurtle(new HandredBlock());
	}

	// �^�[�g���𓮂�������
	public void start() {

		int i;// ���[�v�p1
		int j;// ���[�v�p2
		int k;// ���[�v�p3

		int length = 10;// �l�p�̑傫��
		int margin = 5;// �ׂ̎l�p�Ƃ̊Ԋu

		{// �c��10��J��Ԃ����߂̃��[�v
			i = 1;
			while (i <= 10) {

				{// ����10��J��Ԃ����߂̃��[�v
					j = 1;
					while (j <= 10) {

						{// �l�p�`���������߂̃��[�v
							k = 1;
							while (k <= 4) {
								fd(length);
								rt(90);

								k++;
							}
						}

						{// �l�p�`����������ׂɈړ�
							up();
							rt(90);
							fd(length);
							fd(margin);
							lt(90);
							down();
						}
						j++;
					}

					{// ����񂪏I�����獶���Ɉړ�
						up();
						lt(90);
						fd((length + margin) * 10);
						lt(90);
						fd(length + margin);
						rt(180);
						down();
					}
					i++;
				}
			}

		}
	}

}