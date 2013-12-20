package ClassBlockFileModel;

import java.io.PrintStream;

public class PublicMethodCommandWriter extends BasicModel {

	private PublicMethodInfo method;

	public void setMethods(PublicMethodInfo method) {
		this.method = method;
	}

	public void printCommands(PrintStream out) {
		int lineNum = 0;
		makeIndent(out, lineNum);
		String kind = "command";
		if (method.getreturnType() != null
				&& !method.getreturnType().equals("void")) {
			kind = "function";
		}
		out.println("<BlockGenus name=\"" + method.getName() + "\" kind=\""
				+ kind + "\" initlabel=\"" + method.getName()
				+ "\" color=\"255 0 0\">");

		makeIndent(out, ++lineNum);
		if (method.getParameters() != null) {
			out.println("<BlockConnectors>");

			++lineNum;

			if (method.getreturnType() != null
					&& !method.getreturnType().equals("void")) {
				makeIndent(out, lineNum);
				out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
						+ method.getreturnType() + "\"></BlockConnector>");
			}

			for (String parameter : method.getParameters()) {
				// TODO connector-typeを引数の形で変える　parameterは int
				// xxのような型＋変数名の形で保持されていることに注意されたし
				String parameterType = convertParameterType(parameter
						.substring(0, parameter.indexOf(" ")));
				String parameterName = parameter
						.substring(
								parameter.substring(0, parameter.indexOf(" "))
										.length() + 1, parameter.length());
				makeIndent(out, lineNum);

				out.println("<BlockConnector label=\"" + parameterName
						+ "\" connector-kind=\"socket\" connector-type=\""
						+ convertParameterType(parameterType) + "\">");

				makeIndent(out, lineNum);
				out.println("</BlockConnector>");
				// 引数の設定
			}
			makeIndent(out, --lineNum);
			out.println("</BlockConnectors>");
		}

		makeIndent(out, lineNum);
		out.println("<LangSpecProperties>");
		makeIndent(out, ++lineNum);
		out.println("<LangSpecProperty key=\"vm-cmd-name\" value=\""
				+ method.getName() + "\"></LangSpecProperty>");
		makeIndent(out, lineNum);
		out.println("<LangSpecProperty key=\"stack-type\" value=\"breed\"></LangSpecProperty>");

		makeIndent(out, --lineNum);
		out.println("</LangSpecProperties>");

		makeIndent(out, --lineNum);
		out.println("</BlockGenus>");
		out.println();
	}

	public void printMenuItem(PrintStream out, int lineNumber) {
		makeIndent(out, lineNumber);
		out.println("<BlockGenusMember>" + method.getName()
				+ "</BlockGenusMember>");
	}

	private String convertParameterType(String parameterType) {
		String connectorType = "";
		if (parameterType.equals("int")) {
			connectorType = "number";
		} else if (parameterType.equals("String")) {
			connectorType = "string";
		} else if (parameterType.equals("boolean")) {
			connectorType = "boolean";
		} else if (parameterType.equals("double")) {
			return "double-number";
		} else {
			connectorType = "object";
		}
		return connectorType;
	}

}
