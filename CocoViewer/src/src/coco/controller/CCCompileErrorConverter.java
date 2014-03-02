package src.coco.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import src.coco.model.CCCompileErrorManager;

public class CCCompileErrorConverter extends CCCsvFileLoader {

	private CCCompileErrorManager manager;
	// private int addErrorID;
	private String CAMMA = ",";
	PrintWriter pw;

	public CCCompileErrorConverter(CCCompileErrorManager manager) {
		this.manager = manager;
		// addErrorID = manager.getAllLists().size() + 1;
	}

	public void convertData(String inFileName, String outFileName)
			throws IOException {
		File outfile = new File(outFileName);
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outfile), "sjis")));
		inputHeader(outfile);
		loadData(inFileName);
		pw.flush();
		pw.close();
	}

	private void inputHeader(File outFile) throws IOException {
		StringBuffer buf = new StringBuffer();

		buf.append("ErrorID");
		buf.append(CAMMA);
		buf.append("�t�@�C���p�X");
		buf.append(CAMMA);
		buf.append("��������");
		buf.append(CAMMA);
		buf.append("�C����������");
		buf.append(CAMMA);
		buf.append("�C������");
		// buf.append("ErrorID,�t�@�C����,��������,�C������");
		pw.println(buf.toString());
	}

	protected void separeteData(List<String> lines) throws IOException {
		StringBuffer buf = new StringBuffer();

		// errorID��messageList��manager�ɍ����indexOf���\�b�h�ŉ���
		// ��ɃV���{���Ȃǂ̃`�F�b�N�����Ă���getMessageID������`�ɂ��A�i�V���{���j�ȂǂɑΉ�����
		int errorID = 0;
		String element = "";
		if (lines.get(5) != null) {
			element = "�i" + lines.get(5) + "�j";
		}

		String message = lines.get(3) + element;

		try {
			errorID = manager.getMessagesID(message);
		} catch (Exception e) {
			// ErrorKinds�ɑ��݂��Ȃ��ꍇ�́C�J�E���g���Ȃ�
			return;

			// �V�����G���[���b�Z�[�W���L�^����ꍇ�̏���
			// errorID = addErrorID;
			// manager.put(errorID, 6, message);
			// addErrorID++;
		}

		// spilt�͒���\\�ŋ�؂邱�Ƃ��ł��Ȃ��̂ŁC��������/�ɕϊ�����
		// ���R�ɂ��Ă͌���������邱��
		String filepath = lines.get(2).replace("\\", "/");

		long beginTime = 0;
		if (lines.get(12).indexOf(" ") == -1) {
			beginTime = Long.parseLong(lines.get(12));
		} else {
			beginTime = changeDateStringToLong(lines.get(12));
		}

		long endTime = 0;
		if (lines.get(13).indexOf(" ") == -1) {
			endTime = Long.parseLong(lines.get(13));
		} else {
			endTime = changeDateStringToLong(lines.get(13));
		}

		// correctionTime �� CT�l
		int correctTime = Integer.parseInt(lines.get(18));

		// �f�[�^����������
		buf.append(String.valueOf(errorID));
		buf.append(CAMMA);
		buf.append(filepath);
		buf.append(CAMMA);
		buf.append(String.valueOf(beginTime));
		buf.append(CAMMA);
		buf.append(String.valueOf(endTime));
		buf.append(CAMMA);
		buf.append(String.valueOf(correctTime));
		pw.println(buf.toString());
		// out.write(errorID + "," + filename + "," + beginTime + ","
		// + correctTime + "\n");
	}

	private long changeDateStringToLong(String data) {
		String[] tokenizer = data.split(" ");
		String[] dates = tokenizer[0].split("/");
		String[] times = tokenizer[1].split(":");

		int year = Integer.parseInt(dates[0]);
		int month = Integer.parseInt(dates[1]);
		int day = Integer.parseInt(dates[2]);

		int hour = Integer.parseInt(times[0]);
		int minute = Integer.parseInt(times[1]);
		int second = Integer.parseInt(times[2]);

		Calendar calender = Calendar.getInstance();
		calender.set(year, month, day, hour, minute, second);

		return calender.getTimeInMillis();
	}
}