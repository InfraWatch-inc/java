package infra.watch;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ModeloLinear {
    private List<Dados> conjuntoDados;
    private List<Dados> variaveisDependentes;
    private List<Dados> variaveisIndependentes;
    private String nomeArquivo;

    public ModeloLinear(List<Dados> conjuntoDados, String nomeArquivo) {
        this.conjuntoDados = conjuntoDados;
        this.nomeArquivo = nomeArquivo;
        variaveisDependentes = new ArrayList<>();
        variaveisIndependentes = new ArrayList<>();
        this.organizarDadosModelo();
        this.separarTiposVariaveis();
    }

    private void organizarDadosModelo(){
        // TODO realizar conversões e coisas necessárias
    }

    public Boolean testarModeloLinear() {
        // TODO testa possibilidades de modelos, e armazena os dados do modelo com maior chance, porém esta chance (R2)
        //  deverá ser maior que 70%

        // primeiro utiliza uma variavel dependente e testa o modelo multiplo com o resto
            // se passou, salva
            // se não, só retira uma variável independente
        // caso nn tenha passado por essa variavel dependente, tentar com a proxima o processo
        // caso nn tenha mais variavel dependente, encerra false

        // Dados: 4 observações, 2 variáveis independentes
        double[][] X = {
                {1, 2},
                {2, 1},
                {4, 3},
                {3, 5}
        };

        double[] Y = {5, 6, 10, 12};

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(Y, X); // y, x

        double[] beta = regression.estimateRegressionParameters(); // coeficientes β
        double r2 = regression.calculateRSquared();                // R²

        System.out.printf("Coeficientes: %s\n", java.util.Arrays.toString(beta));
        System.out.printf("R² = %.4f\n", r2);
         return true;
    }

    public void salvarDadosModelo(AmazonS3 s3Client, String bucket){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        // TODO aplicar as colunas e dados de acordo com o arquivo
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(header.get(0),header.get(1),header.get(2),header.get(3),header.get(4),header.get(5),header.get(6)));

        for (Livro livro : livros) {
            livro.setDataPublicacao();
            livro.setPrecoDesconto();

            csvPrinter.printRecord(
                    String.format("%d", livro.getId()),
                    String.format("%s", livro.getNome()),
                    String.format("%s", livro.getAutor()),
                    String.format("%s", livro.getDataPublicacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                    String.format("%s", livro.getEditora()),
                    String.format("%.2f", livro.getPreco()),
                    String.format("%.2f", livro.getPrecoDesconto()),
                    String.format("%.1f", livro.getNota())
            );
        }

        csvPrinter.flush();
        writer.close();

        InputStream csvInputStream = new ByteArrayInputStream(outputStream.toByteArray());

        s3Client.putObject(bucket, this.nomeArquivo, csvInputStream, null);
    }

    private void separarTiposVariaveis(){
        // TODO rodar para separar as variaveis e guardar nas listas
    }

    public List<Dados> getVariaveisDependentes() {
        return variaveisDependentes;
    }

    public List<Dados> getVariaveisIndependentes() {
        return variaveisIndependentes;
    }
}
