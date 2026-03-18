<h1>Technická Dokumentace - Projekt Malování</h1>
<p>Tento dokument slouží jako podrobný technický popis architektury, datových struktur a použitých algoritmů v aplikaci <strong>Malování</strong>. Program byl vyvinut v jazyce <strong>Java (Java SE)</strong> s důrazem na nízkoúrovňovou implementaci grafických operací bez použití komplexních knihoven jako Graphics2D pro samotné kreslení tvarů.</p>

<h1>1. Architektura systému</h1>
<p>Aplikace je postavena na principu oddělení datové logiky od grafického výstupu. Projekt je organizován do následujících balíčků:</p>

<h2>1.1 Balíček <code>models</code> (Datové modely)</h2>
<p>Obsahuje třídy reprezentující geometrické objekty ve vektorové formě. Hierarchie je založena na polymorfismu.</p>
<ul>
<li><strong><code>Polygon.java</code></strong>: Klíčová mateřská třída projektu. Každý nakreslený objekt je v jádru polygonem. Obsahuje:
<ul>
<li>Seznam vrcholů (<code>ArrayList&lt;Point&gt; points</code>).</li>
<li>Vlastnosti: <code>color</code> (barva obrysu), <code>fillColor</code> (barva výplně), <code>lineType</code> (styl čáry reprezentovaný výčtem <code>LineType</code>: SOLID, DOTTED, DASHED), a <code>thickness</code> (tloušťka).</li>
<li>Metodu <code>contains(x, y)</code> implementující Ray Casting algoritmus pro detekci kliknutí dovnitř objektu (využíváno nástroji <strong>Posun</strong> a <strong>Edit bodu</strong>).</li>
</ul>
</li>
<li><strong><code>Line.java</code></strong>: Reprezentuje nástroj <strong>Čára</strong>. Definuje právě dva body a je explicitně neuzavřená (<code>isClosed = false</code>).</li>
<li><strong><code>Rectangle.java</code></strong>: Reprezentuje nástroje <strong>Obd.</strong> a <strong>Čtverec</strong>. Při inicializaci automaticky vygeneruje 4 rohové body z počátečního a koncového bodu.</li>
<li><strong><code>Ellipse.java</code></strong>: Reprezentuje nástroje <strong>Elipsa</strong> a <strong>Kruh</strong>. Udržuje si střed (<code>center</code>) a poloměry (<code>rx</code>, <code>ry</code>) pro přesnou rasterizaci. Zároveň generuje 40 pomocných vrcholů pro potřeby editace a hit-testingu.</li>
<li><strong><code>Point.java</code></strong>: Datová přepravka pro souřadnice X a Y s možností jejich modifikace (settery).</li>
<li><strong><code>LineCanvas.java</code></strong>: Správce scény, který udržuje globální seznam všech vytvořených objektů (<code>ArrayList&lt;Polygon&gt; shapes</code>).</li>
</ul>

<h2>1.2 Balíček <code>rasterizers</code> (Vykreslovací jádro)</h2>
<p>Zajišťuje převod vektorových modelů na pixely v rastru.</p>
<ul>
<li><strong><code>TrivRasterizer.java</code></strong>: Implementuje základní kreslicí algoritmy.
<ul>
<li><strong>Úsečka (<code>rasterize(Line)</code>)</strong>: Inkrementální algoritmus (DDA-like) s podporou tloušťky a přeskakování pixelů pro vzory (tečkování, čárkování).</li>
<li><strong>Elipsa (<code>rasterize(Ellipse)</code>)</strong>: Midpoint/Bresenhamův algoritmus pro matematicky přesné a hladké oválné křivky.</li>
<li><strong>Vektorová výplň (<code>fillPolygon</code>)</strong>: <strong>Scanline</strong> algoritmus uplatňující pravidlo sudá-lichá (Even-Odd) pro vyplnění vnitřku tvarů. Aktivuje se nastavením <strong>Auto-výplň</strong>.</li>
<li><strong>Rastrová výplň (<code>seedFill</code>)</strong>: Iterativní <strong>Flood Fill</strong> algoritmus uplatňující strukturu fronty (<code>Queue</code>) k zamezení StackOverflow. Volá se nástrojem <strong>Kyblík</strong>.</li>
</ul>
</li>
<li><strong><code>CanvasRasterizer.java</code></strong>: Vyšší vrstva, která prochází <code>LineCanvas</code>. Každý objekt vykresluje v logickém pořadí: nejdříve vnitřní výplň, poté obrys, a pokud je objekt aktuálně vybraný (<code>selectedPolygon</code>), dodatečně vykreslí editační úchopy.</li>
</ul>

<h2>1.3 Balíčky <code>rasters</code> a <code>math</code></h2>
<ul>
<li><strong><code>RasterBufferedImage.java</code></strong>: Implementace rozhraní <code>Raster</code> pro přímou manipulaci s pixelovým polem objektu <code>java.awt.image.BufferedImage</code>.</li>
<li><strong><code>AngleCalculator.java</code></strong>: Zajišťuje logiku pro funkci <strong>Snapping</strong> (klávesa Shift). Metody <code>getAngle</code> a <code>getSnappedB</code> přepočítávají pozici kurzoru na nejbližší 45° úhlový krok.</li>
</ul>

<h2>1.4 Třída <code>App.java</code> (Hlavní kontrolér a UI)</h2>
<p>Propojuje logiku s uživatelským rozhraním Swing. Řídí stavy pomocí výčtového typu <code>Tool</code> (LINE, RECTANGLE, SQUARE, ELLIPSE, CIRCLE, POLYGON, EDIT, MOVE, ERASE, DELETE, BUCKET). Obsahuje <code>MouseAdapter</code> pro řízení životního cyklu kreslení (Pressed, Dragged, Released) a synchronizaci UI prvků se zvoleným objektem (<code>syncUIWithSelected()</code>).</p>