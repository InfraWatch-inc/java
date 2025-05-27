package infra.watch;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import com.amazonaws.services.s3.AmazonS3;

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
        this.separarTiposVariaveis();
    }

    private double[][] montarMatrizX(List<Dados> variaveisIndependentes, int numObservacoes) {
        double[][] X = new double[numObservacoes][variaveisIndependentes.size()];
        for (int i = 0; i < variaveisIndependentes.size(); i++) {
            List<String> valores = variaveisIndependentes.get(i).getInformacoes();
            for (int j = 0; j < numObservacoes; j++) {
                try{
                    X[j][i] = Double.parseDouble(valores.get(j));
                } catch (NumberFormatException e) {
                    System.out.println("Erro ao converter valor para double: " + valores.get(j));
                }                
            }
        }
        return X;
    }

    private double[] montarVetorY(Dados dependente, int numObservacoes) {
        double[] Y = new double[numObservacoes];
        for (int i = 0; i < numObservacoes; i++) {
            try{
                Y[i] = Double.parseDouble(dependente.getInformacoes().get(i));
            } catch (NumberFormatException e) {
                System.out.println("Erro ao converter valor para double: " + dependente.getChave());
            }
        }
        return Y;
    }

    public Boolean testarModeloLinear() {
        int numObservacoes = conjuntoDados.get(0).getInformacoes().size();

        for (Dados varDependente : variaveisDependentes) {
            // Lista de independentes disponíveis para esse modelo
            List<Dados> variaveisIndependentesAtuais = new ArrayList<>(variaveisIndependentes);

            while (!variaveisIndependentesAtuais.isEmpty()) {
                try {
                    double[][] X = montarMatrizX(variaveisIndependentesAtuais, numObservacoes);
                    double[] Y = montarVetorY(varDependente, numObservacoes);

                    OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
                    regression.newSampleData(Y, X);

                    double r2 = regression.calculateRSquared();
                    System.out.printf("Tentando com dependente [%s], R² = %.4f\n", varDependente.getChave(), r2);

                    if (r2 >= 0.70) {
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao calcular regressão: " + e.getMessage());
                }

                // Remove a última variável independente e tenta novamente
                variaveisIndependentesAtuais.remove(variaveisIndependentesAtuais.size() - 1);
            }
        }

        return false;
    }

    public void salvarDadosModelo(AmazonS3 s3Client, String bucket) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {

            int linhas = conjuntoDados.get(0).getInformacoes().size();
            for (int i = 0; i < linhas; i++) {
                List<String> linha = new ArrayList<>();
                for (Dados d : conjuntoDados) {
                    linha.add(d.getInformacoes().get(i));
                }
                csvPrinter.printRecord(linha);
            }

            csvPrinter.flush();
            InputStream csvInputStream = new ByteArrayInputStream(outputStream.toByteArray());
            s3Client.putObject(bucket, this.nomeArquivo, csvInputStream, null);

        } catch (Exception e) {
            System.out.println("Erro ao salvar dados no S3: " + e.getMessage());
        }
    }

    private void separarTiposVariaveis(){
        for (Dados dado : conjuntoDados) {
            if (Boolean.TRUE.equals(dado.getDependente())) {
                variaveisDependentes.add(dado);
            } else {
                variaveisIndependentes.add(dado);
            }
        }
    }

    public List<Dados> getVariaveisDependentes() {
        return variaveisDependentes;
    }

    public List<Dados> getVariaveisIndependentes() {
        return variaveisIndependentes;
    }
}
