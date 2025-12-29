package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Utilitario para geracao de codigos QR usando a biblioteca ZXing.
 * 
 * Permite converter texto em imagens QR Code (PNG/JPG) para uso em documentos e
 * etiquetas.
 * Disponibiliza tambem funcionalidades para visualizacao rapida em janela
 * (debug).
 */
public class QRCode {
	/** Tamanho padrao do QR Code (pixels). */
	private static int size = 250;

	/** Cor de fundo padrao. */
	private static Color bgColor = Color.BLACK;

	/** Cor principal (pontos) padrao. */
	private static Color ikColor = Color.WHITE;

	/**
	 * Metodo principal para demonstração/teste rapido.
	 * 
	 * @param args argumentos da linha de comandos
	 * @throws WriterException em caso de erro na geracao do codigo
	 * @throws IOException     em caso de erro na escrita do ficheiro
	 */
	public static void main(String[] args) throws WriterException, IOException {
		String msgText = "bom dia!";
		displayQR(msgText);
		System.out.println("avançou");
		createQR("sauda.png", msgText);
	}

	/**
	 * Gera e exibe o QR Code numa janela.
	 * 
	 * @param qrCodeText o texto a codificar
	 * @throws WriterException se falhar a geracao
	 */
	public static void displayQR(String qrCodeText) throws WriterException {
		display(generateQR(qrCodeText));
	}

	/**
	 * Exibe uma imagem numa janela Swing simples.
	 * 
	 * @param image a imagem a exibir
	 */
	private static void display(BufferedImage image) {
		JFrame frame = new JFrame();
		frame.setTitle("Display");
		frame.setSize(image.getWidth(), image.getHeight());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // WindowConstants.EXIT_ON_CLOSE
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(image));
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Gera o QR Code e guarda-o num ficheiro.
	 * 
	 * @param filePath   caminho do ficheiro de saida
	 * @param qrCodeText texto a codificar
	 * @throws IOException     se falhar a escrita
	 * @throws WriterException se falhar a geracao
	 */
	public static void createQR(String filePath, String qrCodeText) throws IOException, WriterException {
		create(filePath, generateQR(qrCodeText));
	}

	/**
	 * Escreve a imagem bufferizada em disco.
	 * 
	 * @param filePath caminho completo do ficheiro
	 * @param img      objeto de imagem em memoria
	 * @throws IOException erro de escrita
	 */
	private static void create(String filePath, BufferedImage img) throws IOException {
		String fileType = filePath.substring(filePath.indexOf('.') + 1);
		ImageIO.write(img, fileType, new File(filePath));
	}

	/**
	 * Gera a imagem do QR Code com as configuracoes padrao.
	 * 
	 * @param qrCodeText texto a codificar
	 * @return imagem do QR Code
	 * @throws WriterException erro na geracao
	 */
	private static BufferedImage generateQR(String qrCodeText) throws WriterException {
		return generateQR(qrCodeText, size, bgColor, ikColor);
	}

	/**
	 * Gera a imagem do QR Code com configuracoes especificas.
	 * 
	 * @param qrCodeText texto a codificar
	 * @param size       dimensao da imagem
	 * @param bgColor    cor de fundo
	 * @param ikColor    cor principal
	 * @return imagem gerada
	 * @throws WriterException erro na geracao
	 */
	private static BufferedImage generateQR(String qrCodeText, int size, Color bgColor, Color ikColor)
			throws WriterException {
		// Criar a matriz de bytes para o QR Code
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		ByteMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);

		// Converter matriz para BufferedImage
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(ikColor);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);

		// Desenhar o codigo
		graphics.setColor(bgColor);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j) != 0) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		return image;
	}
}