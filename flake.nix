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
        javaVersion = 24;

        jdk = pkgs."temurin-bin-${toString javaVersion}";
        gradle = pkgs.gradle.override {
            javaToolchains = [
                jdk
                pkgs.temurin-bin
            ];

            java = jdk;
        };
       in {
         devShells.default = pkgs.mkShell {
           name = "JDA-Commands";
           packages = with pkgs; [git jdk gradle maven pkgs.temurin-bin];
           JDK24 = jdk;
         };
       };
    };
}