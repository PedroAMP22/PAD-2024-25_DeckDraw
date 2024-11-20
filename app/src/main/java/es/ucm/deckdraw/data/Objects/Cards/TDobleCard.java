package es.ucm.deckdraw.data.Objects.Cards;

public class TDobleCard extends TCard{
    private TCard front;  // Cara frontal de la carta
    private TCard back;   // Cara posterior de la carta

    // Constructor vacío para inicializar ambas caras
    public TDobleCard() {
        super();
        this.front = new TCard();
        this.back = new TCard();
    }

    // Constructor para inicializar front y back con instancias de TCarta
    public TDobleCard(TCard front, TCard back) {
        // Llama al constructor de la clase base para inicializar atributos comunes
        super();
        this.front = front;
        this.back = back;

        // Usa valores comunes entre las caras para inicializar atributos heredados
        setLayout(front.getLayout());
        setColors(front.getColors()); // Por ejemplo, colores comunes en ambas caras
    }

    @Override
    public String getLargeImageUrl(){
        return front.getLargeImageUrl();
    }
    @Override
    public String getNormalImageUrl(){
        return front.getNormalImageUrl();
    }
    @Override
    public String getSmallImageUrl(){
        return front.getSmallImageUrl();
    }

    // Constructor que recibe un objeto TCarta base
    public TDobleCard(TCard base) {
        // Llama al constructor de la clase padre (TCarta) y pasa los atributos
        super(base.getID(),
                base.getLargeImageUrl(),
                base.getNormalImageUrl(),
                base.getSmallImageUrl(),
                base.getArtCropImageUrl(),
                base.getLanguage(),
                base.getLayout(),
                base.getLegal(),
                base.getManaCost(),
                base.getName(),
                base.getPower(),
                base.getToughness(),
                base.getArtist(),
                base.getBorderColor(),
                base.getType(),
                base.getSetName(),
                base.getRarity(),
                base.getCmc(),
                base.getText(),
                base.getColors());

        // Inicializa front y back en null
        this.front = null;
        this.back = null;
    }

    // Getter y Setter para la cara frontal
    public TCard getFront() {
        return front;
    }

    public void setFront(TCard front) {
        this.front = front;
    }

    // Getter y Setter para la cara posterior
    public TCard getBack() {
        return back;
    }

    public void setBack(TCard back) {
        this.back = back;
    }

    @Override
    public void printCardDetails() {
        System.out.println("Detalles de la carta doble:");
        System.out.println("Layout: " + getLayout());
        System.out.println("Colors: " + getColors());

        System.out.println("\nFront Face:");
        if (front != null) {
            front.printCardDetails();
        } else {
            System.out.println("No front face available.");
        }

        System.out.println("\nBack Face:");
        if (back != null) {
            back.printCardDetails();
        } else {
            System.out.println("No back face available.");
        }
    }

    @Override
    public String getCardDetails() {
        StringBuilder details = new StringBuilder();

        details.append("Detalles de la carta doble:\n")
                .append("Layout: ").append(getLayout()).append("\n")
                .append("Colors: ").append(getColors()).append("\n\n");

        details.append("Front Face:\n");
        if (front != null) {
            details.append(front.getCardDetails());
        } else {
            details.append("No front face available.\n");
        }

        details.append("\nBack Face:\n");
        if (back != null) {
            details.append(back.getCardDetails());
        } else {
            details.append("No back face available.\n");
        }

        return details.toString();
    }
}

