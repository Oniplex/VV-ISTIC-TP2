public class TestNestedIf {

    public void process(int[] numbers) {
        for (int num : numbers) {
            if (num > 0) {
                // First level if
                if (num % 2 == 0) {
                    // Second level if
                    if (num > 100) {
                        // Third level if
                        System.out.println("Found a large even number: " + num);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] sampleNumbers = { 1, 2, 50, 150, 200 };
        TestNestedIf example = new TestNestedIf();
        example.process(sampleNumbers);
    }
}
