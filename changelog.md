# AlchemyLib 1.20.1-1.0.27 RELEASE

Changes:
- Refactor FakeItemRenderer a little more because there was a GL state leak with setting shader color that caused tooltips to be semi-transparent despite the shader color being reset. This uses the method used in the recipe book.