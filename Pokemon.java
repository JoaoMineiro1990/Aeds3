public class Pokemon {
        private int number;
        private String name;
        private String type1;
        private String type2;
        private String abilities;
        private int hp;
        private int att;
        private int def;
        private int spa;
        private int spd;
        private int spe;
        private int bst;
        private double mean;
        private double standardDeviation;
        private String generation;
        private String catchRate;
        private double legendary;
        private double megaEvolution;   
        private double height;
        private double weight;
        private double bmi;

        public Pokemon(int number, String name, String type1, String type2, String abilities, int hp, int att, int def, int spa, int spd, int spe, int bst, double mean, double standardDeviation, String generation, String catchRate, double legendary, double megaEvolution, double height, double weight, double bmi) {
                this.number = number;
                this.name = name;
                this.type1 = type1;
                this.type2 = type2;
                this.abilities = abilities;
                this.hp = hp;
                this.att = att;
                this.def = def;
                this.spa = spa;
                this.spd = spd;
                this.spe = spe;
                this.bst = bst;
                this.mean = mean;
                this.standardDeviation = standardDeviation;
                this.generation = generation;
                this.catchRate = catchRate;
                this.legendary = legendary;                     
                this.megaEvolution = megaEvolution;
                this.height = height;
                this.weight = weight;
                this.bmi = bmi;
        }
        // Getters and Setters
        public int getNumber() {
                return number;
        }

        public void setNumber(int number) {
                this.number = number;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getType1() {
                return type1;
        }

        public void setType1(String type1) {
                this.type1 = type1;
        }

        public String getType2() {
                return type2;
        }

        public void setType2(String type2) {
                this.type2 = type2;
        }

        public String getAbilities() {
                return abilities;
        }

        public void setAbilities(String abilities) {
                this.abilities = abilities;
        }

        public int getHp() {
                return hp;
        }

        public void setHp(int hp) {
                this.hp = hp;
        }

        public int getAtt() {
                return att;
        }

        public void setAtt(int att) {
                this.att = att;
        }

        public int getDef() {
                return def;
        }

        public void setDef(int def) {
                this.def = def;
        }

        public int getSpa() {
                return spa;
        }

        public void setSpa(int spa) {
                this.spa = spa;
        }

        public int getSpd() {
                return spd;
        }

        public void setSpd(int spd) {
                this.spd = spd;
        }

        public int getSpe() {
                return spe;
        }

        public void setSpe(int spe) {
                this.spe = spe;
        }

        public int getBst() {
                return bst;
        }

        public void setBst(int bst) {
                this.bst = bst;
        }

        public double getMean() {
                return mean;
        }

        public void setMean(double mean) {
                this.mean = mean;
        }

        public double getStandardDeviation() {
                return standardDeviation;
        }

        public void setStandardDeviation(double standardDeviation) {
                this.standardDeviation = standardDeviation;
        }

        public String getGeneration() {
                return generation;
        }

        public void setGeneration(String generation) {
                this.generation = generation;
        }

        public String getCatchRate() {
                return catchRate;
        }

        public void setCatchRate(String catchRate) {
                this.catchRate = catchRate;
        }

        public double getLegendary() {
                return legendary;
        }

        public void setLegendary(double legendary) {
                this.legendary = legendary;
        }

        public double getMegaEvolution() {
                return megaEvolution;
        }

        public void setMegaEvolution(double megaEvolution) {
                this.megaEvolution = megaEvolution;
        }

        public double getHeight() {
                return height;
        }

        public void setHeight(double height) {
                this.height = height;
        }

        public double getWeight() {
                return weight;
        }

        public void setWeight(double weight) {
                this.weight = weight;
        }

        public double getBmi() {
                return bmi;
        }

        public void setBmi(double bmi) {
                this.bmi = bmi;
        }
}