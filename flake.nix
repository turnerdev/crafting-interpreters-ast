{
  description = "crafting interpreters tree-walk interpreter";
  inputs = { nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable"; };
  outputs = { self, nixpkgs }:
    let
      supportedSystems = [ "x86_64-linux" "x86_64-darwin" ];
      forAllSystems = f:
        nixpkgs.lib.genAttrs supportedSystems (system: f system);
      nixpkgsFor = forAllSystems (system: import nixpkgs { inherit system; });
    in {

      packages = forAllSystems (system:
        let pkgs = nixpkgsFor.${system};
        in {
          lox = pkgs.stdenv.mkDerivation {
            pname = "Lox";
            version = "1.2";
            buildInputs = with pkgs; [ jdk ];
            src = ./.;
            buildPhase = ''
              javac -d $out com/craftinginterpreters/lox/Lox.java
            '';
            installPhase = ''
              mkdir -p $out/bin
              cat > $out/bin/lox <<EOF
              #!${pkgs.stdenv.shell}
              exec ${pkgs.jdk}/bin/java -cp "$out" com.craftinginterpreters.lox.Lox "\$@"
              EOF
              chmod +x $out/bin/lox
            '';
          };
        });

      devShells = forAllSystems (system:
        let pkgs = nixpkgsFor.${system};
        in {
          default = pkgs.mkShell {
            buildInputs = with pkgs; [
              jdk
              google-java-format
              rlwrap
              self.packages.${system}.lox
            ];
          };
        });

      defaultPackage = forAllSystems (system: self.packages.${system}.lox);
    };
}
