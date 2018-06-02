public class CurrentAndBestSolution {


    private Chromosome currentSolution;
    private Chromosome bestSolution;

    public CurrentAndBestSolution(Chromosome currentSolution, Chromosome bestSolution)
    {
        this.currentSolution = currentSolution;
        this.bestSolution = bestSolution;
    }

    public Chromosome getCurrentSolution()
    {
        return currentSolution;
    }

    public Chromosome getBestSolution()
    {
        return bestSolution;
    }

    public void setCurrentSolution(Chromosome currentSolution) {
        this.currentSolution = currentSolution;
    }

    public void setBestSolution(Chromosome bestSolution) {
        this.bestSolution = bestSolution;
    }
}
