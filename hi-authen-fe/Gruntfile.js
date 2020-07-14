/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
module.exports = function (grunt) {
    var dataDir = 'D:/Hien/hiperform/wrapper/httpserver-win64/app/authen/';
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        copy:{
            main: {
                files: [
                  // share
                  {expand: true, src: ['share/js/app/**/*'], dest: dataDir},
                  // web
                  {expand: true, src: ['web/**/*'], dest: dataDir}
                ],
                options: { force: true }
            },
            full: {
                files: [
                  // share
                  {expand: true, src: ['share/**/*'], dest: dataDir},
                  // web
                  {expand: true, src: ['web/**/*'], dest: dataDir}
                ],
                options: { force: true }                
            }
        },
        clean:{
            target: {
                src: [
                    dataDir + 'share',
                    dataDir + 'web'
                ],
                options: { force: true }
            }
        }
    });
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-clean');
};
