package util;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;

import javax.imageio.*;
import javax.swing.*;

/**
 * Utilitário para gestão de imagens em formato binário (BLOB).
 * 
 * Fornece funcionalidades para carregamento, armazenamento, visualização e
 * comparação
 * de ficheiros de imagem. Inclui um algoritmo de comparação pixel-a-pixel para
 * deteção de semelhanças entre imagens.
 * 
 * Suporta conversão entre formatos byte array, Base64 e BufferedImage,
 * facilitando
 * a integração com bases de dados e interfaces gráficas.
 */
public class Foto {
	/**
	 * Array de bytes contendo os dados binários da imagem.
	 */
	byte[] img = null;

	/**
	 * Nome do ficheiro de imagem padrão quando nenhuma foto está disponível.
	 */
	public final static String omissa = "silhueta.jpg";

	/**
	 * Caminho base para localização dos ficheiros de imagem.
	 */
	public static String path = new Configura().getRealPath();

	/**
	 * Método principal para testes e demonstração da funcionalidade.
	 * 
	 * @param args Argumentos de linha de comando (não utilizados).
	 * @throws Exception Se ocorrer erro no carregamento ou processamento das
	 *                   imagens.
	 */
	public static void main(String[] args) throws Exception {
		Foto ft1 = new Foto();
		Foto ft2 = new Foto();
		String f1 = "silhueta.jpg";
		String f2 = "silhueta.png";
		Foto.setPath(path);
		ft1.load(f1);
		ft1.show("Fotografia: " + f1);
		ft2.load(f2);
		ft2.show("Fotografia: " + f2);
		System.out.println("Semelhança: " + ft1.compareTo(ft2) + "%");
	}

	/**
	 * Define o caminho base para localização dos ficheiros de imagem.
	 * 
	 * @param path Caminho absoluto para o diretório de imagens.
	 */
	public static void setPath(String path) {
		Foto.path = path;
	}

	/**
	 * Define os dados binários da imagem.
	 * 
	 * @param img Array de bytes contendo a imagem.
	 */
	public void setFoto(byte[] img) {
		this.img = img;
	}

	/**
	 * Retorna os dados binários da imagem armazenada.
	 * 
	 * @return Array de bytes da imagem.
	 */
	public byte[] getFoto() {
		return img;
	}

	/**
	 * Define os dados da imagem a partir de uma string codificada em Base64.
	 * 
	 * @param encoded64 String Base64 representando a imagem.
	 */
	public void setFoto64(String encoded64) {
		this.img = Base64.getDecoder().decode(encoded64);
	}

	/**
	 * Retorna a imagem codificada em formato Base64.
	 * 
	 * @return String Base64 da imagem.
	 */
	public String getFoto64() {
		return Base64.getEncoder().encodeToString(img);
	}

	/**
	 * Converte um array de bytes numa instância de BufferedImage.
	 * 
	 * @param imageData Dados binários da imagem.
	 * @return BufferedImage criada a partir dos dados.
	 * @throws RuntimeException Se ocorrer erro na leitura dos dados.
	 */
	private static BufferedImage createImageFromBytes(byte[] imageData) {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData);) {
			return ImageIO.read(bais);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retorna a representação BufferedImage da imagem armazenada.
	 * 
	 * @return BufferedImage da foto.
	 */
	public BufferedImage getBufferedImage() {
		return Foto.createImageFromBytes(img);
	}

	/**
	 * Apresenta a imagem numa janela gráfica com título padrão.
	 */
	public void show() {
		show("Fotografia");
	}

	/**
	 * Apresenta a imagem numa janela gráfica com título personalizado.
	 * Se nenhuma imagem estiver carregada, tenta carregar a imagem padrão.
	 * 
	 * @param titulo Título da janela de visualização.
	 */
	public void show(String titulo) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame editorFrame = new JFrame(titulo);
				BufferedImage image = null;
				if (img == null)
					try {
						load(omissa);
					} catch (Exception e) {
						return;
					}
				image = getBufferedImage();
				ImageIcon imageIcon = new ImageIcon(image);
				JLabel jLabel = new JLabel();
				jLabel.setIcon(imageIcon);
				editorFrame.getContentPane().add(jLabel, BorderLayout.CENTER);
				editorFrame.pack();
				editorFrame.setLocationRelativeTo(null);
				editorFrame.setVisible(true);
			}
		});
	}

	/**
	 * Carrega uma imagem a partir de um ficheiro no disco.
	 * 
	 * @param filename Nome do ficheiro de imagem (relativo ao caminho base).
	 * @throws Exception Se ocorrer erro na leitura do ficheiro.
	 */
	public void load(final String filename) throws Exception {
		File file = new File(path + filename);
		FileInputStream fl = new FileInputStream(file);
		img = new byte[(int) file.length()];
		fl.read(img);
		fl.close();
	}

	/**
	 * Grava a imagem armazenada num ficheiro no disco.
	 * 
	 * @param filename Nome do ficheiro de destino (relativo ao caminho base).
	 * @throws Exception Se ocorrer erro na escrita do ficheiro.
	 */
	public void save(final String filename) throws Exception {
		FileOutputStream fos = new FileOutputStream(path + filename);
		fos.write(img);
		fos.close();
	}

	/**
	 * Compara esta imagem com outra e calcula a percentagem de semelhança.
	 * 
	 * Utiliza um algoritmo de comparação pixel-a-pixel que analisa as diferenças
	 * nos componentes RGB de cada pixel. A métrica resultante varia entre 0%
	 * (completamente
	 * diferentes) e 100% (idênticas).
	 * 
	 * As imagens devem ter as mesmas dimensões para serem comparáveis.
	 * 
	 * @param ft Foto a comparar com esta instância.
	 * @return BigDecimal representando a percentagem de semelhança (0-100).
	 * @throws Exception Se as dimensões das imagens não coincidirem ou ocorrer erro
	 *                   no processamento.
	 */
	public BigDecimal compareTo(Foto ft) throws Exception {
		BufferedImage imgA = getBufferedImage();
		BufferedImage imgB = ft.getBufferedImage();
		int width1 = imgA.getWidth();
		int width2 = imgB.getWidth();
		int height1 = imgA.getHeight();
		int height2 = imgB.getHeight();

		if ((width1 != width2) || (height1 != height2)) {
			System.err.println("Erro: As dimensões das imagens não coincidem.");
			return new BigDecimal(-1);
		}

		long difference = 0;

		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				int rgbA = imgA.getRGB(x, y);
				int rgbB = imgB.getRGB(x, y);
				int redA = (rgbA >> 16) & 0xff;
				int greenA = (rgbA >> 8) & 0xff;
				int blueA = (rgbA) & 0xff;
				int redB = (rgbB >> 16) & 0xff;
				int greenB = (rgbB >> 8) & 0xff;
				int blueB = (rgbB) & 0xff;
				difference += Math.abs(redA - redB);
				difference += Math.abs(greenA - greenB);
				difference += Math.abs(blueA - blueB);
			}
		}

		double total_pixels = width1 * height1 * 3;
		double avg_different_pixels = difference / total_pixels;
		double percentage = (avg_different_pixels / 255) * 100;
		BigDecimal bd = BigDecimal.valueOf(100 - percentage);
		return bd.setScale(2, RoundingMode.HALF_UP);
	}
}