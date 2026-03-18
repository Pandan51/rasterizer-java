<h1>Uživatelský návod - Aplikace Malování</h1>
<p>Vítejte v aplikaci <strong>Malování</strong>. Tento program funguje jako plnohodnotný vektorově-rastrový grafický editor. Umožňuje kreslit geometrické objekty (čáry, obdélníky, elipsy, polygony), dodatečně upravovat jejich vlastnosti a vybarvovat plochy.</p>

<h1>1. Nástroje pro kreslení</h1>
<p>Tyto nástroje slouží k vytváření nových objektů na plátně. Tvar vytvoříte stisknutím levého tlačítka myši, tažením a následným uvolněním (s výjimkou polygonu).</p>

<ul>
<li><strong>Čára (<code>Tool.LINE</code>)</strong>: Kreslí rovnou úsečku reprezentovanou modelem <code>Line</code>.</li>
<li><strong>Obd. (<code>Tool.RECTANGLE</code>)</strong>: Vytvoří obdélníkový polygon.</li>
<li><strong>Čtverec (<code>Tool.SQUARE</code>)</strong>: Vytvoří obdélník, u kterého je programem striktně vynucen poměr stran 1:1.</li>
<li><strong>Elipsa (<code>Tool.ELLIPSE</code>)</strong>: Kreslí precizní oválný tvar využitím matematického modelu <code>Ellipse</code>.</li>
<li><strong>Kruh (<code>Tool.CIRCLE</code>)</strong>: Elipsa se shodnými poloměry os X a Y.</li>
<li><strong>Polygon (<code>Tool.POLYGON</code>)</strong>: Klikáním levého tlačítka myši přidáváte na plátno jednotlivé vrcholy. Pro ukončení a automatické uzavření tvaru klikněte pravým tlačítkem nebo stiskněte klávesu <strong>Enter</strong>.</li>
</ul>

<h1>2. Nástroje pro úpravu a manipulaci</h1>
<p>Slouží k interakci s již existujícími tvary. Nástroje využívají techniku Hit-testing pro rozpoznání, na jaký objekt myší míříte.</p>

<ul>
<li><strong>Posun (<code>Tool.MOVE</code>)</strong>: Uchopte libovolný nakreslený objekt (kliknutím na jeho obrys nebo dovnitř jeho plochy) a táhněte jím po plátně.</li>
<li><strong>Edit bodu (<code>Tool.EDIT</code>)</strong>: Umožňuje deformovat tvary. Uchopte modrý editační bod vybraného tvaru a přesuňte jej. <em>Tip: Pokud přesunete bod u elipsy, automaticky se převede na tvarovatelný polygon.</em></li>
<li><strong>Smazani bodu (<code>Tool.ERASE</code>)</strong>: Kliknutím na konkrétní modrý vrchol jej vymažete. Pokud tvaru zbudou méně než 2 vrcholy, program jej automaticky celý smaže.</li>
<li><strong>Smazat obj. (<code>Tool.DELETE</code>)</strong>: Kliknutím na objekt jej kompletně odstraníte z plátna.</li>
<li><strong>Kyblík (<code>Tool.BUCKET</code>)</strong>: Rastrový nástroj. Kliknutím do uzavřené oblasti ji beze zbytku vyplníte barvou vybranou v nastavení "Barva Čáry" (využívá iterativní algoritmus <code>seedFill</code>).</li>
</ul>

<h1>3. Nastavení vlastností tvaru</h1>
<p>Panel vpravo nahoře mění vlastnosti. Důležité: Pokud máte aktuálně <strong>vybraný objekt</strong> (objekt s modrými tečkami), změny se <strong>okamžitě aplikují</strong> na něj. Pokud nemáte vybráno nic, nastavujete vlastnosti pro tvary, které teprve nakreslíte.</p>

<ul>
<li><strong>Barva Čáry (<code>currentColor</code>)</strong>: Změní barvu obrysu objektu (a barvu pro nástroj Kyblík).</li>
<li><strong>Barva Výplně (<code>currentFillColor</code>)</strong>: Změní barvu vnitřní plochy polygonů.</li>
<li><strong>Tloušťka (<code>currentThickness</code>)</strong>: Posuvníkem určíte sílu čáry od 1 do 20 pixelů.</li>
<li><strong>Styl čáry (<code>currentType</code>)</strong>: Roletové menu pro přepínání mezi Plnou (Solid), Tečkovanou (Dotted) a Čárkovanou (Dashed) obrysovou čarou.</li>
<li><strong>Auto-výplň (<code>isFilledMode</code>)</strong>: Zaškrtávací pole. Pokud je zapnuté, aktivuje u objektu Scanline algoritmus, který jej vyplní barvou výplně.</li>
</ul>

<h1>4. Klávesové zkratky (Funkce)</h1>
<ul>
<li><strong>Držení klávesy Shift (Snapping)</strong>: Aktivuje třídu <code>AngleCalculator</code>. Koncový bod kreslené čáry (nebo právě přidávaný bod polygonu) se bude přichytávat pouze k přesným úhlům (0°, 45°, 90°, 135° atd.). Skvělé pro kolmice a rovnoběžky.</li>
<li><strong>Klávesa C (Clear)</strong>: Vyčistí instanci <code>LineCanvas</code> a smaže kompletně celé plátno. Akce je nevratná.</li>
<li><strong>Klávesa Enter</strong>: Alternativa k pravému tlačítku myši pro uzavření a dokončení nástroje Polygon.</li>
</ul>