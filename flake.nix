{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs = {
    self,
    flake-parts,
    ...
  } @ inputs:
    flake-parts.lib.mkFlake {inherit inputs;} {
      systems = ["x86_64-linux"];

      perSystem = {
        config,
        lib,
        pkgs,
        system,
        ...
      }: let
        javaVersion = 23;

        jdk = pkgs."temurin-bin-${toString javaVersion}";
        gradle = pkgs.gradle.override {
            javaToolchains = [
                jdk
            ];

            java = jdk;
        };
       in {
         devShells.default = pkgs.mkShell {
           name = "Jack";
           packages = with pkgs; [git jdk gradle maven];
         };
       };
    };
}