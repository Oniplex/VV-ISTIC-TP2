# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

TCC and LCC produce the same value when all method pairs that are indirectly connected are also directly connected. In other words, if the methods form a single strongly cohesive cluster (e.g., a complete graph where every pair of methods shares at least one instance variable), then TCC = LCC. Another trivial case is when the class is completely non-cohesive and no pairs of methods share variables, resulting in TCC = LCC = 0. Since TCC measures direct connections and LCC measures both direct and indirect connections, LCC can never be lower than TCC. It is always the case that LCC ≥ TCC.

---

**When TCC = LCC**:  
   TCC will be equal to LCC if every pair of methods that is connected indirectly is also directly connected. This situation typically occurs in two cases:
   - **Complete Graph of Cohesion**: Each method shares at least one instance variable with every other method. Thus, every pair is both directly connected and indirectly connected, making TCC = LCC.  
   
   - **No Connectivity at All**: If there are no instance variables shared among methods, there are no connections (edges) in the graph. In that case, TCC = LCC = 0.

**Example Class Where TCC = LCC**:  
Here is a simple example in Java:

```java
public class CohesiveClass {
    private int x;
    private int y;

    // All methods use instance variables x or y
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int sum() {
        // Uses both x and y
        return x + y;
    }

    public int product() {
        // Uses both x and y
        return x * y;
    }
}
```

**Analysis of This Example**:  
- Methods: getX, getY, sum, product  
- Pairs: (getX,getY), (getX,sum), (getX,product), (getY,sum), (getY,product), (sum,product)  
Each pair of methods shares at least one instance variable. For example:
- getX and getY each access different variables but are connected through sum or product (in this case, because sum and product use both x and y, they create a complete “bridge” connecting all methods directly).

Actually, in this class, every pair is directly connected by shared usage of at least `x` or `y`:
- getX uses {x}
- getY uses {y}
- sum uses {x, y}
- product uses {x, y}

getX and getY do not share a variable directly, but since sum and product connect to both, it still forms a single connected component. If we consider direct connections only on shared variables:
- getX and sum share x
- getX and product share x
- getY and sum share y
- getY and product share y
- sum and product share {x,y}
