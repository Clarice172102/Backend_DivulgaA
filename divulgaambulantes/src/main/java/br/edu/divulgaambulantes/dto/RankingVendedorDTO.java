package br.edu.divulgaambulantes.dto;

public class RankingVendedorDTO {

    private String sellerId;
    private String fullName;
    private String email;

    private long totalProdutos;
    private long produtosDenunciados;

    private double pctDenuncias;

    private long totalClicks;
    private long totalLikes;
    private long totalFavoritos;
    private long totalReviews;
    private long totalReviewViews;

    private double scorePonderado;

    public RankingVendedorDTO() {
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getTotalProdutos() {
        return totalProdutos;
    }

    public void setTotalProdutos(long totalProdutos) {
        this.totalProdutos = totalProdutos;
    }

    public long getProdutosDenunciados() {
        return produtosDenunciados;
    }

    public void setProdutosDenunciados(long produtosDenunciados) {
        this.produtosDenunciados = produtosDenunciados;
    }

    public double getPctDenuncias() {
        return pctDenuncias;
    }

    public void setPctDenuncias(double pctDenuncias) {
        this.pctDenuncias = pctDenuncias;
    }

    public long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalFavoritos() {
        return totalFavoritos;
    }

    public void setTotalFavoritos(long totalFavoritos) {
        this.totalFavoritos = totalFavoritos;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public long getTotalReviewViews() {
        return totalReviewViews;
    }

    public void setTotalReviewViews(long totalReviewViews) {
        this.totalReviewViews = totalReviewViews;
    }

    public double getScorePonderado() {
        return scorePonderado;
    }

    public void setScorePonderado(double scorePonderado) {
        this.scorePonderado = scorePonderado;
    }
}