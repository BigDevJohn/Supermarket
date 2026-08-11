package jv.supermarket.image;

public class ImagemDTO {

    private String nomeArquivo;
    private String tipoArquivo;

    private String urlDownload;

    public ImagemDTO(String nomeArquivo, String tipoArquivo, String urlDownload) {
        this.nomeArquivo = nomeArquivo;
        this.tipoArquivo = tipoArquivo;
        this.urlDownload = urlDownload;
    }

    public ImagemDTO() {
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }

}
