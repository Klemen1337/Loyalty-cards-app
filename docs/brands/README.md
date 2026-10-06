# Brand list for the loyalty-cards app

`brands.json` (same data in `brands.csv`) lists 48 brands: 38 that operate in Slovenia plus 10 big European chains for travellers.
Fields: id, name, category, region, website, loyaltyProgram, primaryColor, secondaryColor, colorSource, logo, logoSource, logoLicense.

## Colours
- **simple-icons**: the official brand colour as curated by the Simple Icons project. Most reliable.
- **brandfetch**: palette scraped from the brand's website. Usually right, but sometimes picks a site accent instead of the logo colour. Ones worth a quick eyeball against the real card: Tuš, Merkur, Hofer (secondary), Douglas, Mladinska knjiga, Pepco.
- **approximate**: from the logo, not a published value (Lesnina, some secondaries).

## Logos
- `logos/*.svg` (17 files) come from Simple Icons v16.34.0. They are single-colour 24x24 glyphs: tint them with `primaryColor` or white on a `primaryColor` card. The SVG drawings are CC0 (see `logos/SIMPLE-ICONS-LICENSE.md`).
- `hofer.svg` is the ALDI Süd mark (it says "ALDI"). Hofer uses the same design with "HOFER", so swap it or show a text tile.
- The other 31 brands (Mercator, SPAR, Tuš, Petrol, Merkur, dm partner brands, etc.) have no openly licensed logo file I could fetch. The app should fall back to a coloured tile with the brand name or initials.

## Trademark notes (not legal advice)
- CC0 covers the SVG drawing, not the mark. Every logo is still the brand's trademark.
- Showing a store's logo so a user can recognise *their own* card is a common, generally tolerated nominative use (Stocard, Klarna, Google Wallet all do it). Risk rises if the app looks endorsed, so: no "official" wording, add a line like "All trademarks belong to their owners; this app is not affiliated with them" in About and the store listing.
- Don't use brand logos in the app icon, screenshots' hero area, or the store listing title. Apple (guideline 5.2.1) and Google Play (impersonation/IP policy) reject apps that appear to be by or endorsed by a brand without permission.
- Keep logos removable: if a brand sends a takedown, delete its entry in an update. Ship the list as data so that is a one-line change.
- Safest path for the unlogoed brands: coloured tile + name. Optional: email the Slovenian chains (Mercator, SPAR, Tuš, Petrol) asking for permission and an official SVG.
