<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 
<xsl:template match="/">
  <html>
  <body>
    <div>
      <xsl:attribute name="class">
        <xsl:text>container-fluid page</xsl:text>
      </xsl:attribute>

      <!-- Page header -->
      <div>
        <xsl:attribute name="class">
          <xsl:text>header</xsl:text>
        </xsl:attribute>

        <h2>
          <xsl:attribute name="class">
            <xsl:text>text-center</xsl:text>
          </xsl:attribute>
          SimpleDiff Repository - PR #2 - Code Change Summary
        </h2>
      </div>

      <!-- Main content -->
      <div>
        <xsl:attribute name="class">
          <xsl:text>row</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="id">
          <xsl:text>main</xsl:text>
        </xsl:attribute>

        <!-- File diff per file content -->
        <xsl:for-each select="document/file">

          <!-- Collapse menu for file-->
          <button>
            <xsl:attribute name="class">
              <xsl:text>btn btn-secondary file-item text-left</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="type">
              <xsl:text>button</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="data-toggle">
              <xsl:text>collapse</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="data-target">
              <xsl:value-of select="concat('#file-', position())"/>
            </xsl:attribute>
            <xsl:attribute name="aria-expanded">
              <xsl:text>false</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="aria-controls">
              <xsl:text>collapseExample</xsl:text>
            </xsl:attribute>
            <xsl:value-of select="name"/>
          </button>

          <!-- div containing diff contents -->
          <div>
            <xsl:attribute name="class">
              <xsl:text>col-12 collapse</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="id">
              <xsl:value-of select="concat('file-', position())"/>
            </xsl:attribute>

            
            <div>
              <xsl:attribute name="class">
                <xsl:text>row change</xsl:text>
              </xsl:attribute>

              <button>
                <xsl:attribute name="class">
                  <xsl:text>btn btn-secondary change-item text-left</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="type">
                  <xsl:text>button</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-toggle">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-target">
                  <xsl:value-of select="concat('#change-pkg-', position())"/>
                </xsl:attribute>
                <xsl:attribute name="aria-expanded">
                  <xsl:text>false</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="aria-controls">
                  <xsl:value-of select="concat('#change-pkg-', position())"/>
                </xsl:attribute>
                Package Declaration Changes
              </button>

              <!-- Package declaration collapsible diff -->
              <div>
                <xsl:attribute name="class">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="id">
                  <xsl:value-of select="concat('change-pkg-', position())"/>
                </xsl:attribute>

                <div>
                  <xsl:attribute name="class">
                    <xsl:text>card card-body</xsl:text>
                  </xsl:attribute>
                  <xsl:value-of select="change-pkg"/>
                </div>
              </div>

            </div> <!-- End change section -->



            <div>
              <xsl:attribute name="class">
                <xsl:text>row change</xsl:text>
              </xsl:attribute>

              <button>
                <xsl:attribute name="class">
                  <xsl:text>btn btn-secondary change-item text-left</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="type">
                  <xsl:text>button</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-toggle">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-target">
                  <xsl:value-of select="concat('#change-import-', position())"/>
                </xsl:attribute>
                <xsl:attribute name="aria-expanded">
                  <xsl:text>false</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="aria-controls">
                  <xsl:value-of select="concat('#change-import-', position())"/>
                </xsl:attribute>
                Import Declaration Changes
              </button>

              <!-- Import declaration collapsible diff -->
              <div>
                <xsl:attribute name="class">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="id">
                  <xsl:value-of select="concat('change-import-', position())"/>
                </xsl:attribute>

                <div>
                  <xsl:attribute name="class">
                    <xsl:text>card card-body</xsl:text>
                  </xsl:attribute>
                  <xsl:for-each select="change-import/change">
                    <div>
                      <xsl:value-of select="change-text"/>
                    </div>
                  </xsl:for-each>
                </div>
              </div>

            </div> <!-- End change section -->



            <div>
              <xsl:attribute name="class">
                <xsl:text>row change</xsl:text>
              </xsl:attribute>

              <button>
                <xsl:attribute name="class">
                  <xsl:text>btn btn-secondary change-item text-left</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="type">
                  <xsl:text>button</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-toggle">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-target">
                  <xsl:value-of select="concat('#change-modifier-', position())"/>
                </xsl:attribute>
                <xsl:attribute name="aria-expanded">
                  <xsl:text>false</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="aria-controls">
                  <xsl:text>collapseExample</xsl:text>
                </xsl:attribute>
                Modifier Changes
              </button>

              <!-- Modifier declaration collapsible diff -->
              <div>
                <xsl:attribute name="class">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="id">
                  <xsl:value-of select="concat('change-modifier-', position())"/>
                </xsl:attribute>

                <div>
                  <xsl:attribute name="class">
                    <xsl:text>card card-body</xsl:text>
                  </xsl:attribute>
                  <xsl:for-each select="change-modifier/change">
                    <div>
                      <xsl:value-of select="change-text"/>
                    </div>
                    <div style="background-color: white; border: solid">
                      <xsl:value-of select="change-src"/>
                    </div>
                    <div style="background-color: white; border: solid">
                      <xsl:value-of select="change-dst"/>
                    </div>
                  </xsl:for-each>
                </div>
              </div>

            </div> <!-- End change section -->


            <div>
              <xsl:attribute name="class">
                <xsl:text>row change</xsl:text>
              </xsl:attribute>

              <button>
                <xsl:attribute name="class">
                  <xsl:text>btn btn-secondary change-item text-left</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="type">
                  <xsl:text>button</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-toggle">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-target">
                  <xsl:value-of select="concat('#change-method-', position())"/>
                </xsl:attribute>
                <xsl:attribute name="aria-expanded">
                  <xsl:text>false</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="aria-controls">
                  <xsl:value-of select="concat('#change-method-', position())"/>
                </xsl:attribute>
                Method Declaration Changes
              </button>

              <!-- Method declaration collapsible diff -->
              <div>
                <xsl:attribute name="class">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="id">
                  <xsl:value-of select="concat('change-method-', position())"/>
                </xsl:attribute>

                <div>
                  <xsl:attribute name="class">
                    <xsl:text>card card-body</xsl:text>
                  </xsl:attribute>
                  <xsl:for-each select="change-method/change">
                    <div>
                      <xsl:value-of select="change-text"/>
                    </div>
                    <div style="background-color: white; border: solid">
                      <xsl:value-of select="change-src"/>
                    </div>
                    <div style="background-color: white; border: solid">
                      <xsl:value-of select="change-dst"/>
                    </div>
                  </xsl:for-each>
                </div>
              </div>

            </div> <!-- End change section -->



            <div>
              <xsl:attribute name="class">
                <xsl:text>row change</xsl:text>
              </xsl:attribute>

              <button>
                <xsl:attribute name="class">
                  <xsl:text>btn btn-secondary change-item text-left</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="type">
                  <xsl:text>button</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-toggle">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="data-target">
                  <xsl:value-of select="concat('#change-raw-', position())"/>
                </xsl:attribute>
                <xsl:attribute name="aria-expanded">
                  <xsl:text>false</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="aria-controls">
                  <xsl:text>collapseExample</xsl:text>
                </xsl:attribute>
                Raw Diff
              </button>

              <!-- Raw collapsible diff -->
              <div>
                <xsl:attribute name="class">
                  <xsl:text>collapse</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="id">
                  <xsl:value-of select="concat('change-raw-', position())"/>
                </xsl:attribute>

                <div>
                  <xsl:attribute name="class">
                    <xsl:text>card card-body</xsl:text>
                  </xsl:attribute>
                  <xsl:for-each select="change-raw/change">
                    <div>
                    <xsl:value-of select="change-text" disable-output-escaping="yes"/>
                  </div>
                    <div style="background-color: white; border: solid">
                      <xsl:value-of select="change-src"/>
                    </div>
                    <div style="background-color: white; border: solid">
                      <xsl:value-of select="change-dst"/>
                    </div>
                  </xsl:for-each>
                </div>
              </div>

            </div> <!-- End change section -->




          </div> <!-- End diff contents -->

        </xsl:for-each> <!-- End per file content -->

      </div> <!-- End main content -->
    </div>
  </body>
    <link rel="stylesheet" href="dist\diff.css"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous"/>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

  </html>
  </xsl:template>
  </xsl:stylesheet>